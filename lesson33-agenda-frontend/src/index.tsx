import React from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router";
import { AuthProvider } from "./AuthContext";
import { Layout } from "./components/Layout";
import { Login } from "./components/Login";
import { Register } from "./components/Register";
import { UserProfile } from "./components/UserProfile";
import { EventsList } from "./components/EventsList";
import { EventDetails } from "./components/EventDetails";
import { CreateEvent } from "./components/CreateEvent";
import { ProtectedRoute } from "./components/ProtectedRoute";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: <EventsList />,
      },
      {
        path: "login",
        element: <Login />,
      },
      {
        path: "register",
        element: <Register />,
      },
      {
        path: "me",
        element: (
          <ProtectedRoute>
            <UserProfile />
          </ProtectedRoute>
        ),
      },
      {
        path: "create-event",
        element: (
          <ProtectedRoute>
            <CreateEvent />
          </ProtectedRoute>
        ),
      },
      {
        path: "events/:eventId",
        element: <EventDetails />,
      },
    ],
  },
]);

createRoot(document.getElementById("container")!).render(
  <AuthProvider>
    <RouterProvider router={router} />
  </AuthProvider>
);
