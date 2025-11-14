import React, { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

const root = createRoot(document.getElementById("container")!);

root.render(<MyApp></MyApp>);

function MyApp() {
  const [interval, setInterval] = useState(1000);
  const [counterInterval, setCounterInterval] = useState(interval);
  return (
    <>
      <AutoCounter interval={counterInterval}></AutoCounter>
      <hr></hr>
      <strong>Interval = </strong>
      <input
        value={interval}
        onChange={(e) => setInterval(Number(e.target.value))}
      ></input>
      <button onClick={() => setCounterInterval(interval)}>Submit</button>
    </>
  );
}

function AutoCounter({ interval }: { interval: number }) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    const tid = setTimeout(() => {
      setCount(count + 1);
    }, interval);
    return () => clearTimeout(tid)
  }, [interval, count]);
  return (
    <>
      <strong>Count: {count}</strong>
    </>
  );
}
