import React, { act, useContext } from "react";

import {
  Context,
  createContext,
  ReactElement,
  ReactNode,
  useReducer,
} from "react";

type AuthStage = "logged-out" | "logging-in" | "logged-in" | "logging-out";

type AuthState = {
  inPass: string;
  inUsername: string;
  authUsername: string | undefined;
  error: string | undefined;
  stage: AuthStage;
};

export type AuthAction =
  | { type: "input-change"; username: string; pass: string }
  | { type: "login" }
  | { type: "logout" }
  | { type: "success" }
  | { type: "error"; message: string };

function authReducer(current: AuthState, action: AuthAction): AuthState {
  switch (current.stage) {
    case "logged-out": {
      switch (action.type) {
        case "input-change":
          return {
            ...current,
            inPass: action.pass,
            inUsername: action.username,
          };
        case "login":
          return { ...current, stage: "logging-in" };
      }
    }
    case "logging-in":
      switch (action.type) {
        case "error":
          return { ...current, stage: "logged-out", error: action.message };
        case "success":
          return {
            ...current,
            stage: "logged-in",
            authUsername: current.inUsername,
            error: undefined,
          };
      }
    case "logged-in":
      switch (action.type) {
        case "logout":
          return { ...current, stage: "logging-out" };
      }
    case "logging-out":
      switch (action.type) {
        case "error":
          return { ...current, stage: "logged-in", error: action.message };
        case "success":
          return {
            ...current,
            stage: "logged-out",
            authUsername: undefined,
            error: undefined,
          };
      }
    default:
      throw Error(`Illegal action ${action.type} for state ${current.stage}`);
  }
}

type AuthContextType = {
  authState: AuthState | undefined;
  dispatch: (action: AuthAction) => void;
};

/**
 * API for consumers read the context: e.g. useContext(AuthContext)
 */
const AuthContext = createContext<AuthContextType>({
  authState: undefined,
  dispatch: () => Error("Unsupported operation!"),
});

export function useAuthUsername() {
  const { authState } = useContext(AuthContext);
  return authState?.authUsername;
}

export function useAuthError() {
  const { authState } = useContext(AuthContext);
  return authState?.error;
}

export function useAuthCredentials() {
  const { authState } = useContext(AuthContext);
  if (authState == undefined) {
    throw Error("Illegal authentication state");
  }
  return [authState.inUsername, authState.inPass];
}

export function useAuthStage() {
  const { authState } = useContext(AuthContext);
  if (authState == undefined) {
    throw Error("Illegal authentication state");
  }
  return authState.stage;
}

export function useAuthDispatch() {
  const { dispatch } = useContext(AuthContext);
  return dispatch;
}

/*
 * API for provider: <AuthProvider></>
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [current, dispatch] = useReducer(authReducer, {
    authUsername: undefined,
    inPass: "",
    inUsername: "",
    stage: "logged-out",
    error: undefined,
  });
  return (
    <AuthContext value={{ authState: current, dispatch: dispatch }}>
      {children}
    </AuthContext>
  );
}
