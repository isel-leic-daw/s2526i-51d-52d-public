import React, { ReactNode, useContext } from "react";
import {
  AuthAction,
  AuthProvider,
  useAuthCredentials,
  useAuthDispatch,
  useAuthError,
  useAuthStage,
  useAuthUsername,
} from "./AuthContext";

import { createRoot } from "react-dom/client";
import "./styles.css";

const root = createRoot(document.getElementById("container")!);

root.render(<MyApp></MyApp>);

function MyApp() {
  return (
    <AuthProvider>
      <section className="panel-light">
        <LoginForm></LoginForm>
        <hr></hr>
        <ErrorAlert></ErrorAlert>
      </section>
      <WelcomePanel />
    </AuthProvider>
  );
}

function ErrorAlert() {
  const error = useAuthError();
  return <section>{error ? <strong>ERROR: {error}</strong> : <></>}</section>;
}

function WelcomePanel() {
  const authUsername = useAuthUsername();
  return (
    <section>{authUsername ? <h1>Welcome {authUsername}</h1> : <></>}</section>
  );
}

function LoginForm() {
  const [username, pass] = useAuthCredentials();
  const stage = useAuthStage();
  const dispatch: (action: AuthAction) => void = useAuthDispatch();
  async function loginHandler() {
    dispatch({ type: "login" });
    const res = await authenticate(username, pass);
    if (res != undefined) {
      dispatch({ type: "success" });
    } else {
      dispatch({ type: "error", message: "Username or password invalid!" });
    }
  }
  async function logoutHandler() {
    dispatch({ type: "logout" });
    await delay(1000)
    dispatch({ type: "success" });
  }
  return (
    <>
      <label>
        username{": "}
        <input
          required
          disabled={stage != "logged-out"}
          value={username}
          onChange={(e) =>
            dispatch({
              type: "input-change",
              pass: pass,
              username: e.target.value,
            })
          }
        />
      </label>
      <br></br>
      <label>
        password{": "}
        <input
          required
          disabled={stage != "logged-out"}
          value={pass}
          type="password"
          onChange={(e) =>
            dispatch({
              type: "input-change",
              pass: e.target.value,
              username: username,
            })
          }
        />
      </label>
      <br></br>
      <Button disabled={stage != "logged-out"} onClick={() => loginHandler()}>
        Log in
      </Button>
      <Button disabled={stage != "logged-in"} onClick={() => logoutHandler()}>
        Log out
      </Button>
    </>
  );
}

function Button({
  children,
  disabled,
  onClick,
}: {
  children: ReactNode;
  disabled: boolean;
  onClick: () => void;
}) {
  const className = "button-light";
  return (
    <button className={className} disabled={disabled} onClick={onClick}>
      {children}
    </button>
  );
}

/*********************************************
 * Auxiliary Functions emulating authenticate
 */

function delay(delayInMs: number) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(undefined), delayInMs);
  });
}

async function authenticate(
  username: string,
  password: string
): Promise<string | undefined> {
  await delay(3000);
  if ((username == "alice" || username == "bob") && password == "1234") {
    return username;
  }
  return undefined;
}
