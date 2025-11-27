import { Link } from "react-router";
import { Event } from "../types";
import { useFetch } from "../hooks/useFetch";
import "../styles/App.css";

export function EventsList() {
  const state = useFetch<Event[]>("/events");

  if (state.type === "begin" || state.type === "loading") {
    return <div className="events-list-loading">Loading events...</div>;
  }

  if (state.type === "error") {
    return <div className="events-list-error">{state.error.message}</div>;
  }

  const events = state.payload;

  return (
    <div className="events-list-container">
      <h2>All Events</h2>
      {events.length === 0 ? (
        <p className="events-list-empty">
          No events found. Create one to get started!
        </p>
      ) : (
        <div className="events-list-grid">
          {events.map((event) => (
            <Link
              key={event.id}
              to={`/events/${event.id}`}
              className="events-list-link"
            >
              <div className="events-list-card">
                <h3>{event.title}</h3>
                <p>{event.description}</p>
                <div className="events-list-card-meta">
                  <span>Organizer: {event.organizer.name}</span>
                  <span>Type: {event.selectionType}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
