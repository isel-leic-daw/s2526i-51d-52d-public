import React, {
  Context,
  createContext,
  ReactNode,
  useContext,
  useState,
} from "react";

type ThemeColor = "light" | "dark";

type Theme = {
  color: ThemeColor;
  setColor: (color: ThemeColor) => void;
};

const ThemeContext: Context<Theme> = createContext<Theme>({
  color: "light",
  setColor: () => {
    throw Error("Not implemented!");
  },
});

export function useThemeColor(): ThemeColor {
  const { color } = useContext(ThemeContext);
  return color;
}

export function useSetColor(): (color: ThemeColor) => void {
  const { setColor } = useContext(ThemeContext);
  return setColor;
}

export function ThemeProvider({
  initialColor,
  children,
}: {
  initialColor: ThemeColor;
  children: ReactNode;
}) {
  const [color, setColor] = useState(initialColor);
  return (
    <ThemeContext value={{ color: color, setColor: setColor }}>
      {children}
    </ThemeContext>
  );
}
