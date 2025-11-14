import React, {
  Context,
  createContext,
  ReactElement,
  ReactNode,
  useContext,
  useState,
} from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

const root = createRoot(document.getElementById("container")!);

root.render(<MyApp></MyApp>);

type Theme = "light" | "dark";

const ThemeContext: Context<Theme> = createContext<Theme>("light");

function MyApp(): ReactElement {
  const [theme, setTheme] = useState<Theme>("light");
  return (
    <>
      <ThemeContext value={theme}>
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
      </ThemeContext>
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
  const theme: Theme = useContext(ThemeContext);
  const className = "panel-" + theme;
  return (
    <section className={className}>
      <h1>{title}</h1>
      {children}
    </section>
  );
}

function Button({ children }: { children: ReactNode }) {
  const theme: Theme = useContext(ThemeContext);
  const className = "button-" + theme;
  return <button className={className}>{children}</button>;
}
