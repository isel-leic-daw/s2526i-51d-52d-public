import { useState } from "react";
import { useNavigate } from "react-router";
import { TimeSlot as TimeSlotType, Participant, User } from "../types";
import { api, ApiError } from "../api";
import "../styles/App.css";

interface TimeSlotProps {
  eventId: number;
  slot: TimeSlotType;
  participants: Participant[];
  selectionType: "SINGLE" | "MULTIPLE";
  currentUser: User | null;
}

async function handleSlotAction(
  action: "join" | "leave",
  eventId: number,
  timeSlotId: number,
  user: User | null,
  navigate: ReturnType<typeof useNavigate>,
  setIsLoading: (loading: boolean) => void,
  setError: (error: string) => void
) {
  if (!user) {
    navigate("/login");
    return;
  }

  setError("");
  setIsLoading(true);
  try {
    if (action === "join") {
      await api.addParticipantToTimeSlot(eventId, timeSlotId);
    } else {
      await api.removeParticipantFromTimeSlot(eventId, timeSlotId);
    }
    setIsLoading(false);
  } catch (err) {
    const errorMessage =
      err instanceof ApiError ? err.message : `Failed to ${action} time slot`;
    setError(errorMessage);
  }
}

export function TimeSlotView({
  eventId,
  slot,
  participants,
  selectionType,
  currentUser,
}: TimeSlotProps) {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  
  const isMultiple = selectionType === "MULTIPLE";
  const isSingle = selectionType === "SINGLE";
  const isAllocated = isSingle && slot.owner;
  const userIsParticipant =
    isMultiple && participants.some((p) => p.user.id === currentUser?.id);

  const handleJoin = () => {
    handleSlotAction("join", eventId, slot.id, currentUser, navigate, setIsLoading, setError);
  };

  const handleLeave = () => {
    handleSlotAction("leave", eventId, slot.id, currentUser, navigate, setIsLoading, setError);
  };

  return (
    <div className="event-details-slot-card">
      {error && <div className="event-details-action-error">{error}</div>}
      <div className="event-details-slot-content">
        <div className="event-details-slot-info">
          <div>
            <strong>Start:</strong> {new Date(slot.startTime).toLocaleString()}
          </div>
          <div>
            <strong>Duration:</strong> {slot.durationInMinutes} minutes
          </div>

          {isSingle && slot.owner && (
            <div className="event-details-slot-owner">
              <strong>Allocated to:</strong> {slot.owner.name} (
              {slot.owner.email})
            </div>
          )}

          {isSingle && !slot.owner && (
            <div className="event-details-slot-available">
              <strong>Status:</strong> Available
            </div>
          )}

          {isMultiple && (
            <div className="event-details-slot-participants">
              <strong>Participants ({participants.length}):</strong>
              {participants.length === 0 ? (
                <span className="event-details-slot-participants-empty">
                  No participants yet
                </span>
              ) : (
                <ul className="event-details-slot-participants-list">
                  {participants.map((participant) => (
                    <li key={participant.id}>
                      {participant.user.name} ({participant.user.email})
                    </li>
                  ))}
                </ul>
              )}
            </div>
          )}
        </div>

        <div className="event-details-slot-actions">
          {currentUser && isSingle && !isAllocated && (
            <button
              onClick={handleJoin}
              className="event-details-join-btn"
              disabled={isLoading}
            >
              {isLoading ? "Joining..." : "Join"}
            </button>
          )}

          {currentUser &&
            isSingle &&
            isAllocated &&
            slot.owner?.id === currentUser.id && (
              <button
                onClick={handleLeave}
                className="event-details-join-btn"
                disabled={isLoading}
              >
                {isLoading ? "Leaving..." : "Leave"}
              </button>
            )}

          {currentUser && isMultiple && !userIsParticipant && (
            <button
              onClick={handleJoin}
              className="event-details-join-btn"
              disabled={isLoading}
            >
              {isLoading ? "Joining..." : "Join"}
            </button>
          )}

          {currentUser && isMultiple && userIsParticipant && (
            <button
              onClick={handleLeave}
              className="event-details-join-btn"
              disabled={isLoading}
            >
              {isLoading ? "Leaving..." : "Leave"}
            </button>
          )}

          {isSingle && isAllocated && slot.owner?.id !== currentUser?.id && (
            <div className="event-details-slot-badge-allocated">Allocated</div>
          )}
        </div>
      </div>
    </div>
  );
}
