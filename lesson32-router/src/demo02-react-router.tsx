import React from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  Link,
  Outlet,
  RouterProvider,
  useParams,
} from "react-router";

let router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      {
        path: "/about",
        Component: About,
      },
      {
        path: "/cities/:city",
        Component: Cities,
      },
    ],
  },
]);

createRoot(document.getElementById("container")!).render(
  <RouterProvider router={router} />
);

function Root() {
  return (
    <>
      <ul>
        <li>
          <Link to="/">Home</Link>
        </li>
        <li>
          <a href="/about">
            About (<strong>with anchor</strong>)
          </a>
        </li>
        <li>
          <Link to="/about">About</Link>
        </li>
        <li>
          <Link to="/cities/new-york">Cities</Link>
        </li>
        <Outlet></Outlet>
      </ul>
    </>
  );
}

function About() {
  return (
    <>
      <p>Created in DAW course of 2025</p>
      <p>by Miguel Gamboa</p>
    </>
  );
}

function Cities() {
  const { city } = useParams();
  const key = city as keyof typeof cities;
  return (
    <>
      <p>List of favorite cities worldwide</p>
      <ol>
        {Object.entries(cities).map(([key, value]) => (
          <li key={key}>
            <Link to={`../${key}`} relative="path">{value.name}</Link>
          </li>
        ))}
      </ol>
      <h3>{cities[key].name}</h3>
      <p>{cities[key].description}</p>
    </>
  );
}

const cities = {
  lisbon: {
    name: "Lisbon",
    description: "Nice hills and views. Warm weather.",
  },
  "new-york": {
    name: "New York",
    description:
      "Iconic skyline, diverse neighborhoods, and vibrant city life.",
  },
  rome: {
    name: "Rome",
    description:
      "Historic landmarks, ancient ruins, and rich cultural heritage.",
  },
  sydney: {
    name: "Sydney",
    description:
      "Beautiful harbour, famous beaches, and a relaxed outdoor lifestyle.",
  },
};
