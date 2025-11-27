import { useEffect } from "react";

// SSE Message types
type SlotUpdateAction = "UserJoined" | "UserLeft";

// SSE Message data
interface SlotUpdateData {
  eventId: number;
  slotId: number;
  action: SlotUpdateAction;
  userId: number;
  userName: string;
  userEmail: string;
  participantId: number | null;
}

// SSE Message
export interface SSEMessage {
  id: number;
  data: SlotUpdateData;
}

export function useEventListener(
  eventId: string | undefined,
  onMessage: (message: SSEMessage) => void
) {
  useEffect(() => {
    if (!eventId) return;

    const eventSource = new EventSource(`/api/events/${eventId}/listen`);

    eventSource.onmessage = (event) => {
      try {
        const message: SSEMessage = JSON.parse(event.data);
        onMessage(message);
      } catch (error) {
        console.error("Error parsing SSE message:", error);
      }
    };

    eventSource.onerror = (error) => {
      console.error("SSE Error:", error);
      eventSource.close();
    };

    // Return cleanup function
    return () => {
      eventSource.close();
    };
  }, [eventId, onMessage]);
}
