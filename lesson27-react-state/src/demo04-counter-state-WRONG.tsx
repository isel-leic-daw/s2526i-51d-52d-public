import * as React from "react";
import * as ReactDom from "react-dom/client";

const root = ReactDom.createRoot(document.getElementById("container")!);

// DO NOT use Closures to capture state inside React Components
let count = 0;

function Counter() {
  return (
    <>
      <button
        onClick={() => {
          count--;
          // DON'T do this => Do not call render() explicitly!!
          mainRender();
        }}
      >
        -
      </button>
      {count}
      <button
        onClick={() => {
          count++;
          // DON'T do this => Do not call render() explicitly!!
          mainRender();
        }}
      >
        +
      </button>
    </>
  );
}
function mainRender() {
  root.render(
    <div>
        <Counter></Counter>
        <br></br>
        <Counter></Counter>
    </div>
  );
}

mainRender();
