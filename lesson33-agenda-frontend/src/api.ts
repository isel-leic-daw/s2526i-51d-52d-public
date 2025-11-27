import {
  EventInput,
  TimeSlot,
  TimeSlotInput,
  UserCreateTokenInputModel,
  UserCreateTokenOutputModel,
  UserInput,
} from "./types";
import { getErrorDescription } from "./errorDescriptions";

const API_BASE_URL = "/api";

class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

export function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function fetchApi<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  await delay(1000)
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({ title: "Unknown error" }));
    const errorMessage = error.title
      ? getErrorDescription(error.title)
      : response.statusText;
    throw new ApiError(response.status, errorMessage);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const api = {
  // Users
  async createUser(input: UserInput): Promise<string> {
    const response = await fetch(`${API_BASE_URL}/users`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(input),
    });

    if (!response.ok) {
      const error = await response
        .json()
        .catch(() => ({ title: "Unknown error" }));
      const errorMessage = error.title
        ? getErrorDescription(error.title)
        : response.statusText;
      throw new ApiError(response.status, errorMessage);
    }

    return response.headers.get("Location") || "";
  },

  async createToken(
    input: UserCreateTokenInputModel
  ): Promise<UserCreateTokenOutputModel> {
    return fetchApi<UserCreateTokenOutputModel>("/users/token", {
      method: "POST",
      body: JSON.stringify(input),
    });
  },

  async logout(): Promise<void> {
    return fetchApi<void>("/logout", {
      method: "POST",
      headers: getAuthHeaders(),
    });
  },

  async getMe(): Promise<any> {
    return fetchApi<any>("/me", {
      headers: getAuthHeaders(),
    });
  },

  // Events
  async getAllEvents(): Promise<any[]> {
    return fetchApi<any[]>("/events");
  },

  async getEventDetails(eventId: number): Promise<any> {
    return fetchApi<any>(`/events/${eventId}/details`);
  },

  async createEvent(input: EventInput): Promise<number> {
    return fetchApi<number>("/events", {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(input),
    });
  },

  // TimeSlots
  async createFreeTimeSlot(
    eventId: number,
    input: TimeSlotInput
  ): Promise<TimeSlot> {
    return fetchApi<TimeSlot>(`/events/${eventId}/timeslots`, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(input),
    });
  },

  async addParticipantToTimeSlot(
    eventId: number,
    timeSlotId: number
  ): Promise<TimeSlot> {
    return fetchApi<TimeSlot>(
      `/events/${eventId}/timeslots/${timeSlotId}/participants`,
      {
        method: "PUT",
        headers: getAuthHeaders(),
      }
    );
  },

  async removeParticipantFromTimeSlot(
    eventId: number,
    timeSlotId: number
  ): Promise<TimeSlot> {
    return fetchApi<TimeSlot>(
      `/events/${eventId}/timeslots/${timeSlotId}/participants`,
      {
        method: "DELETE",
        headers: getAuthHeaders(),
      }
    );
  },
};

export { ApiError };

/*********************************************
 * Auxiliary Functions emulating IO latency
 */
function delay(delayInMs: number) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(undefined), delayInMs);
  });
}
