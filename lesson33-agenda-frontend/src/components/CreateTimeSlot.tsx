import React from "react";
import { api, ApiError } from "../api";
import { TimeSlot, TimeSlotInput } from "../types";
import "../styles/App.css";

interface CreateTimeSlotProps {
  eventId: number;
  onSlotCreated: (slot: TimeSlot) => void;
}

type State = {
  startTime: string;
  duration: number;
  error: string | undefined;
  stage: "editing" | "posting" | "succeed" | "failed";
};

type Action =
  | { type: "input-change"; startTime: string; duration: number }
  | { type: "post" }
  | { type: "success" }
  | { type: "error"; message: string };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "input-change":
      return {
        ...state,
        startTime: action.startTime,
        duration: action.duration,
      };
    case "post":
      return {
        ...state,
        stage: "posting",
        error: undefined,
      };
    case "success":
      return {
        startTime: "",
        duration: 60,
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

const initialState: State = {
  startTime: "",
  duration: 60,
  error: undefined,
  stage: "editing",
};

export function CreateTimeSlot({ eventId, onSlotCreated }: CreateTimeSlotProps) {
  const [state, dispatch] = React.useReducer(reducer, initialState);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    dispatch({ type: "post" });

    const input: TimeSlotInput = {
      startTime: state.startTime,
      durationInMinutes: state.duration,
    };

    try {
      const newSlot = await api.createFreeTimeSlot(eventId, input);
      onSlotCreated(newSlot);
      dispatch({ type: "success" });
    } catch (err) {
      if (err instanceof ApiError) {
        dispatch({ type: "error", message: err.message });
      } else {
        dispatch({ type: "error", message: "Failed to create time slot" });
      }
    }
  };

  return (
    <>
      {state.error && <div className="event-details-action-error">{state.error}</div>}
      <form onSubmit={handleSubmit} className="event-details-add-form">
        <div className="event-details-form-group">
          <label className="event-details-form-label">
            Start Time:
            <input
              type="datetime-local"
              value={state.startTime}
              onChange={(e) =>
                dispatch({
                  type: "input-change",
                  startTime: e.target.value,
                  duration: state.duration,
                })
              }
              required
              className="event-details-form-input"
              disabled={state.stage === "posting"}
            />
          </label>
        </div>
        <div className="event-details-form-group">
          <label className="event-details-form-label">
            Duration (minutes):
            <input
              type="number"
              value={state.duration}
              onChange={(e) =>
                dispatch({
                  type: "input-change",
                  startTime: state.startTime,
                  duration: Number(e.target.value),
                })
              }
              required
              min="15"
              step="15"
              className="event-details-form-input"
              disabled={state.stage === "posting"}
            />
          </label>
        </div>
        <button
          type="submit"
          className="event-details-create-btn"
          disabled={state.stage === "posting"}
        >
          {state.stage === "posting" ? "Creating..." : "Create Time Slot"}
        </button>
      </form>
    </>
  );
}
