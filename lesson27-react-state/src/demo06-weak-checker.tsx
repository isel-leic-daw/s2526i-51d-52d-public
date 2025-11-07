import { useState } from "react";
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { worstPasswords } from "./worst-passwords";

const container: HTMLElement = document.getElementById("container")!;
const root = ReactDOM.createRoot(container);
root.render(<WeakChecker></WeakChecker>);

type ValidationStage = "typing" | "validated" | "submitted" | "reset";
type PasswordStrength = "none" | "weak" | "medium" | "strong";

function calculateStrength(password: string, isValid: boolean): PasswordStrength {
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

function WeakChecker() {
  const [pass, setPass] = useState("");
  const [error, setError] = useState<string | undefined>(undefined);
  const [stage, setStage] = useState<ValidationStage>("typing");
  const [strength, setStrength] = useState<PasswordStrength>("none");
  const [attempts, setAttempts] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const [lastSubmittedPass, setLastSubmittedPass] = useState<string | undefined>(undefined);

  function inputHandler(event: React.ChangeEvent<HTMLInputElement>) {
    const input = event.target.value;
    setPass(input);
    setStage("typing");
    setError(undefined);
    
    // Validate
    const res: string | true = validatePassword(input);
    const isValid = res === true;
    setError(isValid ? undefined : res);
    setStage("validated");
    setStrength(calculateStrength(input, isValid));
  }

  function handleSubmit() {
    if (error) {
      setStage("validated");
      setAttempts(attempts + 1);
    } else {
      setStage("submitted");
      setLastSubmittedPass(pass);
      setAttempts(attempts + 1);
    }
  }

  function handleReset() {
    setPass("");
    setError(undefined);
    setStage("reset");
    setStrength("none");
    setAttempts(0);
    setShowPassword(false);
  }

  return (
    <div>
      <h2>Password Validator</h2>
      <div>
        <div>
          Password:
          <input
            value={pass}
            onChange={inputHandler}
            type={showPassword ? "text" : "password"}
          />
          <button type="button" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? "Hide" : "Show"}
          </button>
        </div>

        {pass && (
          <div>
            <strong>Strength:</strong> {strength.toUpperCase()}
          </div>
        )}

        {error && (
          <p>{error}</p>
        )}

        <div>
          <strong>Stage:</strong> {stage}
        </div>

        <div>
          <strong>Attempts:</strong> {attempts}
        </div>

        <button onClick={handleSubmit} disabled={!!error || !pass}>
          Submit
        </button>
        <button type="button" onClick={handleReset}>
          Reset
        </button>
      </div>

      {stage === "submitted" && (
        <div>
          ✓ Password submitted successfully!
        </div>
      )}

      {lastSubmittedPass && stage === "reset" && (
        <div>
          Last submitted: {lastSubmittedPass.replace(/./g, "•")}
        </div>
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
  return true;
}
