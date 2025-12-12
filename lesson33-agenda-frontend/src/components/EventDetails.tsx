import React, { useCallback, useEffect, useReducer } from "react";
import { useParams } from "react-router";
import { api, ApiError } from "../api";
import {
  TimeSlot,
  Participant,
  User,
  EventDetails as EventDetailsType,
} from "../types";
import { useAuth } from "../AuthContext";
import { TimeSlotView } from "./TimeSlotView";
import { CreateTimeSlot } from "./CreateTimeSlot";
import { useEventListener, SSEMessage } from "../hooks/useEventListener";
import "../styles/App.css";

// State type
type State = {
  eventDetails: EventDetailsType | null;
  showAddSlot: boolean;
  isLoading: boolean;
  error: string | null;
};

// Action types
type Action =
  | {
      type: "set-participants";
      participantsBySlot: { [key: number]: Participant[] };
    }
  | { type: "toggle-add-slot" }
  | { type: "add-time-slot"; slot: TimeSlot }
  | { type: "load"; isLoading: boolean }
  | { type: "error"; error: string | null }
  | {
      type: "set-event-details";
      eventDetails: EventDetailsType;
    }
  | {
      type: "user-joined";
      slotId: number;
      user: User;
      participantId: number | null;
    }
  | {
      type: "user-left";
      slotId: number;
      userId: number;
      participantId: number | null;
    };

// Reducer function
function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "set-participants":
      return {
        ...state,
        eventDetails: state.eventDetails
          ? {
              ...state.eventDetails,
              participantsBySlot: action.participantsBySlot,
            }
          : null,
      };
    case "toggle-add-slot":
      return { ...state, showAddSlot: !state.showAddSlot };
    case "add-time-slot":
      return {
        ...state,
        eventDetails: state.eventDetails
          ? {
              ...state.eventDetails,
              timeSlots: [...state.eventDetails.timeSlots, action.slot],
            }
          : null,
        showAddSlot: false,
      };
    case "load":
      return { ...state, isLoading: action.isLoading };
    case "error":
      return { ...state, error: action.error, isLoading: false };
    case "set-event-details":
      return {
        ...state,
        eventDetails: action.eventDetails,
        isLoading: false,
        error: null,
      };
    case "user-joined":
      return handleUserJoined(state, action);
    case "user-left":
      return handleUserLeft(state, action);
    default:
      return state;
  }
}

// Helper functions for SSE updates
function handleUserJoined(
  state: State,
  action: Extract<Action, { type: "user-joined" }>
): State {
  if (!state.eventDetails) return state;

  // For SINGLE timeslots, update the owner field
  const updatedTimeSlots = state.eventDetails.timeSlots.map((slot) => {
    if (
      slot.id === action.slotId &&
      state.eventDetails!.event.selectionType === "SINGLE"
    ) {
      return { ...slot, owner: action.user };
    }
    return slot;
  });

  // For MULTIPLE timeslots, add to participants list
  let newParticipantsBySlot = state.eventDetails.participantsBySlot;
  if (
    state.eventDetails.event.selectionType === "MULTIPLE" &&
    action.participantId !== null
  ) {
    const participants =
      state.eventDetails.participantsBySlot[action.slotId] || [];
    const updatedParticipants = [
      ...participants,
      {
        id: action.participantId,
        user: action.user,
        slot: updatedTimeSlots.find((s) => s.id === action.slotId)!,
      },
    ];
    newParticipantsBySlot = {
      ...state.eventDetails.participantsBySlot,
      [action.slotId]: updatedParticipants,
    };
  }

  return {
    ...state,
    eventDetails: {
      ...state.eventDetails,
      timeSlots: updatedTimeSlots,
      participantsBySlot: newParticipantsBySlot,
    },
  };
}

function handleUserLeft(
  state: State,
  action: Extract<Action, { type: "user-left" }>
): State {
  if (!state.eventDetails) return state;

  // For SINGLE timeslots, clear the owner field
  const updatedTimeSlots = state.eventDetails.timeSlots.map((slot) => {
    if (
      slot.id === action.slotId &&
      state.eventDetails!.event.selectionType === "SINGLE"
    ) {
      return { ...slot, owner: null };
    }
    return slot;
  });

  // For MULTIPLE timeslots, remove from participants list
  let newParticipantsBySlot = state.eventDetails.participantsBySlot;
  if (state.eventDetails.event.selectionType === "MULTIPLE") {
    const participants =
      state.eventDetails.participantsBySlot[action.slotId] || [];
    const updatedParticipants = participants.filter(
      (p) => p.user.id !== action.userId
    );
    newParticipantsBySlot = {
      ...state.eventDetails.participantsBySlot,
      [action.slotId]: updatedParticipants,
    };
  }

  return {
    ...state,
    eventDetails: {
      ...state.eventDetails,
      timeSlots: updatedTimeSlots,
      participantsBySlot: newParticipantsBySlot,
    },
  };
}

// Initial state
const initialState: State = {
  eventDetails: null,
  showAddSlot: false,
  isLoading: true,
  error: null,
};

// Data loading function
async function loadEventData(
  eventId: string,
  dispatch: React.Dispatch<Action>
) {
  dispatch({ type: "load", isLoading: true });
  try {
    const data: EventDetailsType = await api.getEventDetails(Number(eventId));
    dispatch({
      type: "set-event-details",
      eventDetails: data,
    });
  } catch (err) {
    if (err instanceof ApiError) {
      dispatch({ type: "error", error: err.message });
    } else {
      dispatch({ type: "error", error: "Failed to load event details" });
    }
  }
}

export function EventDetails() {
  const { eventId } = useParams<{ eventId: string }>();
  const [state, dispatch] = useReducer(reducer, initialState);
  const { user } = useAuth();
  const sseMessaheHandler = useCallback((message: SSEMessage) => {
    const { action, slotId, userId, userName, userEmail, participantId } =
      message.data;

    switch (action) {
      case "UserJoined":
        dispatch({
          type: "user-joined",
          slotId,
          user: { id: userId, name: userName, email: userEmail },
          participantId,
        });
        break;
      case "UserLeft":
        dispatch({
          type: "user-left",
          slotId,
          userId,
          participantId,
        });
        break;
    }
  }, [eventId])

  // Load event data
  useEffect(() => {
    if (!eventId) return;
    loadEventData(eventId, dispatch);
  }, [eventId]);

  // SSE setup
  useEventListener(eventId, sseMessaheHandler);

  if (state.isLoading) {
    return <div className="event-details-loading">Loading event...</div>;
  }

  if (state.error || !state.eventDetails) {
    return (
      <div className="event-details-error">
        {state.error || "Event not found"}
      </div>
    );
  }

  const { event, timeSlots, participantsBySlot } = state.eventDetails;

  return (
    <div className="event-details-container">
      <div className="event-details-header">
        <h2>{event.title}</h2>
        <p>{event.description}</p>
        <div className="event-details-meta">
          <span>Organizer: {event.organizer.name}</span>
          <span>Selection Type: {event.selectionType}</span>
        </div>
      </div>

      <div className="event-details-timeslots">
        <div className="event-details-timeslots-header">
          <h3>Time Slots</h3>
          {user && user.id === event.organizer.id && (
            <button
              onClick={() => dispatch({ type: "toggle-add-slot" })}
              className="event-details-add-btn"
            >
              {state.showAddSlot ? "Cancel" : "Add Time Slot"}
            </button>
          )}
        </div>

        {state.showAddSlot && (
          <CreateTimeSlot
            eventId={Number(eventId!)}
            onSlotCreated={(slot) => {
              dispatch({ type: "add-time-slot", slot });
              if (event.selectionType === "MULTIPLE") {
                dispatch({
                  type: "set-participants",
                  participantsBySlot: {
                    ...participantsBySlot,
                    [slot.id]: [],
                  },
                });
              }
            }}
          />
        )}

        {timeSlots.length === 0 ? (
          <p className="event-details-empty">No time slots available yet.</p>
        ) : (
          <div className="event-details-timeslots-grid">
            {timeSlots.map((slot) => (
              <TimeSlotView
                key={slot.id}
                eventId={Number(eventId!)}
                slot={slot}
                participants={participantsBySlot[slot.id] || []}
                selectionType={event.selectionType}
                currentUser={user}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
