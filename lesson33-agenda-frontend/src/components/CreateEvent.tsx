import React, { useReducer } from "react";
import { useNavigate } from "react-router";
import { api, ApiError } from "../api";
import { useAuth } from "../AuthContext";
import "../styles/App.css";

// State type
type State = {
  title: string;
  description: string;
  selectionType: "SINGLE" | "MULTIPLE";
  error: string | undefined;
  stage: "editing" | "posting" | "succeed" | "failed";
};

// Action types
type Action =
  | { type: "input-change"; title: string; description: string; selectionType: "SINGLE" | "MULTIPLE" }
  | { type: "post" }
  | { type: "success" }
  | { type: "error"; message: string };

// Reducer function
function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "input-change":
      return {
        ...state,
        title: action.title,
        description: action.description,
        selectionType: action.selectionType,
      };
    case "post":
      return {
        ...state,
        stage: "posting",
        error: undefined,
      };
    case "success":
      return {
        title: "",
        description: "",
        selectionType: "SINGLE",
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
  title: "",
  description: "",
  selectionType: "SINGLE",
  error: undefined,
  stage: "editing",
};

export function CreateEvent() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const { user } = useAuth();
  const navigate = useNavigate();

  if (!user) {
    navigate("/login");
    return null;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    dispatch({ type: "post" });

    try {
      const eventId = await api.createEvent({
        title: state.title,
        description: state.description,
        selectionType: state.selectionType,
      });
      dispatch({ type: "success" });
      navigate(`/events/${eventId}`);
    } catch (err) {
      if (err instanceof ApiError) {
        dispatch({ type: "error", message: err.message });
      } else {
        dispatch({
          type: "error",
          message: "An error occurred while creating the event",
        });
      }
    }
  };

  return (
    <div className="create-event-container">
      <div className="create-event-card">
        <h2>Create New Event</h2>
        <form onSubmit={handleSubmit}>
          <div className="create-event-form-group">
            <label className="create-event-form-label">
              Title:
              <input
                type="text"
                value={state.title}
                onChange={(e) =>
                  dispatch({ 
                    type: "input-change", 
                    title: e.target.value,
                    description: state.description,
                    selectionType: state.selectionType
                  })
                }
                required
                className="create-event-form-input"
              />
            </label>
          </div>
          <div className="create-event-form-group">
            <label className="create-event-form-label">
              Description:
              <textarea
                value={state.description}
                onChange={(e) =>
                  dispatch({
                    type: "input-change",
                    title: state.title,
                    description: e.target.value,
                    selectionType: state.selectionType
                  })
                }
                required
                rows={4}
                className="create-event-form-textarea"
              />
            </label>
          </div>
          <div className="create-event-form-group">
            <label className="create-event-form-label">
              Selection Type:
              <select
                value={state.selectionType}
                onChange={(e) =>
                  dispatch({
                    type: "input-change",
                    title: state.title,
                    description: state.description,
                    selectionType: e.target.value as "SINGLE" | "MULTIPLE",
                  })
                }
                className="create-event-form-select"
              >
                <option value="SINGLE">Single</option>
                <option value="MULTIPLE">Multiple</option>
              </select>
            </label>
          </div>
          {state.error && (
            <div className="create-event-error">{state.error}</div>
          )}
          <button
            type="submit"
            disabled={state.stage === "posting"}
            className="create-event-submit-btn"
          >
            {state.stage === "posting" ? "Creating..." : "Create Event"}
          </button>
        </form>
      </div>
    </div>
  );
}
