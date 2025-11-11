import { useState, useEffect } from "react";
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { worstPasswords } from "./worst-passwords";

const container: HTMLElement = document.getElementById("container")!;
const root = ReactDOM.createRoot(container);
root.render(<WeakChecker></WeakChecker>);

type ValidationStage = "typing" | "validated" | "submitted" | "reset";
type PasswordStrength = "none" | "weak" | "medium" | "strong";

type State = {
  pass: string;
  error: string | undefined;
  stage: ValidationStage;
  strength: PasswordStrength;
  attempts: number;
  showPassword: boolean;
  lastSubmittedPass: string | undefined;
};

type Action =
  | { type: "input-change"; pass: string }
  | { type: "validate"; pass: string; res: true | string }
  | { type: "submit" }
  | { type: "toggle-visibility" }
  | { type: "reset" };

function calculateStrength(
  password: string,
  isValid: boolean
): PasswordStrength {
  if (!password) return "none";
  if (!isValid) return "weak";

  let score = 0;
  if (password.length >= 12) score++;
  if (/[A-Z].*[A-Z]/.test(password)) score++; // 2+ uppercase
  if (/[0-9].*[0-9]/.test(password)) score++; // 2+ numbers
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score++; // special char

  if (score >= 3) return "strong";
  if (score >= 1) return "medium";
  return "weak";
}

function reduce(state: State, action: Action): State {
  switch (action.type) {
    case "input-change":
      return {
        ...state,
        pass: action.pass,
        stage: "typing",
        error: undefined,
      };

    case "validate": {
      const isValid = action.res === true;
      const strength = calculateStrength(action.pass, isValid);
      return {
        ...state,
        pass: action.pass,
        error: action.res === true ? undefined : action.res,
        stage: "validated",
        strength,
      };
    }

    case "submit": {
      if (state.error) {
        return {
          ...state,
          stage: "validated",
          attempts: state.attempts + 1,
        };
      }
      return {
        ...state,
        stage: "submitted",
        lastSubmittedPass: state.pass,
        attempts: state.attempts + 1,
      };
    }

    case "toggle-visibility":
      return {
        ...state,
        showPassword: !state.showPassword,
      };

    case "reset":
      return {
        pass: "",
        error: undefined,
        stage: "reset",
        strength: "none",
        attempts: 0,
        showPassword: false,
        lastSubmittedPass: state.lastSubmittedPass,
      };

    default:
      return state;
  }
}

function WeakChecker() {
  const [state, dispatch] = React.useReducer(reduce, {
    pass: "",
    error: undefined,
    stage: "typing",
    strength: "none",
    attempts: 0,
    showPassword: false,
    lastSubmittedPass: undefined,
  });

  useEffect(() => {
    const tid = setTimeout(() => {
      // Validate after state update
      const res: string | true = validatePassword(state.pass);
      dispatch({ type: "validate", pass: state.pass, res });
    }, 1000);
    return () => clearTimeout(tid);
  }, [state.pass]);

  return (
    <div>
      <h2>Password Validator</h2>
      <div>
        <div>
          Password:
          <input
            value={state.pass}
            onChange={(e) =>
              dispatch({ type: "input-change", pass: e.target.value })
            }
            type={state.showPassword ? "text" : "password"}
          />
          <button
            type="button"
            onClick={() => dispatch({ type: "toggle-visibility" })}
          >
            {state.showPassword ? "Hide" : "Show"}
          </button>
        </div>

        {state.pass && (
          <div>
            <strong>Strength:</strong> {state.strength.toUpperCase()}
          </div>
        )}

        {state.error && <p>{state.error}</p>}

        <div>
          <strong>Stage:</strong> {state.stage}
        </div>

        <div>
          <strong>Attempts:</strong> {state.attempts}
        </div>

        <button
          onClick={() => dispatch({ type: "submit" })}
          disabled={!!state.error || !state.pass}
        >
          Submit
        </button>
        <button type="button" onClick={() => dispatch({ type: "reset" })}>
          Reset
        </button>
      </div>

      {state.stage === "submitted" && (
        <div>✓ Password submitted successfully!</div>
      )}

      {state.lastSubmittedPass && state.stage === "reset" && (
        <div>Last submitted: {state.lastSubmittedPass.replace(/./g, "•")}</div>
      )}
    </div>
  );
}

function validatePassword(password: string): true | string {
  const minLength = 8;
  const commonWords = worstPasswords.filter((pass) =>
    password.toLocaleLowerCase().includes(pass)
  );
  if (commonWords.length > 0) {
    return `Don't use ${commonWords[0].toUpperCase()} on your password!`;
  }
  if (password.length < minLength) {
    return `Password must be at least ${minLength} characters long.`;
  }
  if (!/[A-Z]/.test(password)) {
    return "Password must contain at least one uppercase letter.";
  }
  if (!/[a-z]/.test(password)) {
    return "Password must contain at least one lowercase letter.";
  }
  if (!/[0-9]/.test(password)) {
    return "Password must contain at least one number.";
  }
  // if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
  //     return "Password must contain at least one special character.";
  // }
  // If all checks pass
  return true;
}
