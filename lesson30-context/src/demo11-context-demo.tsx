import React, {
  ReactElement,
  ReactNode,
} from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";
import { ThemeProvider, useSetColor, useThemeColor } from "./ThemeContext";

const root = createRoot(document.getElementById("container")!);

root.render(<MyApp></MyApp>);


function MyApp(): ReactElement {
  const theme = useThemeColor();
  const setTheme = useSetColor();
  return (
    <>
      <ThemeProvider initialColor={"dark"}>
        <AuthForm></AuthForm>
        <label>
          <input
            type="checkbox"
            checked={theme === "dark"}
            onChange={(e) => {
              setTheme(e.target.checked ? "dark" : "light");
            }}
          />
          Use dark mode
        </label>
      </ThemeProvider>
    </>
  );
}

type PanelProps = {
  title: string;
  children?: ReactNode;
};

function AuthForm() {
  return (
    <Panel title="Welcome">
      <Button>Signup</Button>
      <Button>Log in</Button>
    </Panel>
  );
}

function Panel({ title, children }: PanelProps) {
  const theme = useThemeColor();
  const className = "panel-" + theme;
  return (
    <section className={className}>
      <h1>{title}</h1>
      {children}
    </section>
  );
}

function Button({ children }: { children: ReactNode }) {
  const theme = useThemeColor();
  const className = "button-" + theme;
  return <button className={className}>{children}</button>;
}
