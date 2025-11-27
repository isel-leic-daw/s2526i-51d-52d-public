import React, { useReducer } from "react";
import { useNavigate } from "react-router";
import { api, ApiError } from "../api";
import { useAuth } from "../AuthContext";
import "../styles/App.css";

// State type
type State = {
  email: string;
  password: string;
  error: string | undefined;
  stage: "editing" | "posting" | "succeed" | "failed";
};

// Action types
type Action =
  | { type: "input-change"; email: string; password: string }
  | { type: "post" }
  | { type: "success" }
  | { type: "error"; message: string };

// Reducer function
function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "input-change":
      return {
        ...state,
        email: action.email,
        password: action.password,
      };
    case "post":
      return {
        ...state,
        stage: "posting",
        error: undefined,
      };
    case "success":
      return {
        email: "",
        password: "",
        error: undefined,
        stage: "succeed",
      };
    case "error":
      return {
        ...state,
        stage: "failed",
        error: action.message,
      };
    default:
      return state;
  }
}

// Initial state
const initialState: State = {
  email: "",
  password: "",
  error: undefined,
  stage: "editing",
};

export function Login() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    dispatch({ type: "post" });

    try {
      const response = await api.createToken({
        email: state.email,
        password: state.password,
      });
      await login(response.token);
      dispatch({ type: "success" });
      navigate("/");
    } catch (err) {
      if (err instanceof ApiError) {
        dispatch({ type: "error", message: err.message });
      } else {
        dispatch({
          type: "error",
          message: "An error occurred during login",
        });
      }
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="login-form-group">
            <label className="login-form-label">
              Email:
              <input
                type="email"
                name="email"
                value={state.email}
                onChange={(e) =>
                  dispatch({ 
                    type: "input-change", 
                    email: e.target.value,
                    password: state.password
                  })
                }
                required
                autoComplete="email"
                className="login-form-input"
              />
            </label>
          </div>
          <div className="login-form-group">
            <label className="login-form-label">
              Password:
              <input
                type="password"
                name="password"
                value={state.password}
                onChange={(e) =>
                  dispatch({ 
                    type: "input-change",
                    email: state.email,
                    password: e.target.value
                  })
                }
                required
                autoComplete="current-password"
                className="login-form-input"
              />
            </label>
          </div>
          {state.error && <div className="login-error">{state.error}</div>}
          <button
            type="submit"
            disabled={state.stage === "posting"}
            className="login-submit-btn"
          >
            {state.stage === "posting" ? "Logging in..." : "Login"}
          </button>
        </form>
      </div>
    </div>
  );
}
