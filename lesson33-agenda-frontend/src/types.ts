// API Types
export interface User {
  id: number;
  name: string;
  email: string;
}

export interface Event {
  id: number;
  title: string;
  description: string;
  organizer: User;
  selectionType: "SINGLE" | "MULTIPLE";
}

export interface TimeSlot {
  id: number;
  eventId: number;
  startTime: string;
  durationInMinutes: number;
  owner?: User | null;
}

export interface Participant {
  id: number;
  user: User;
  slot: TimeSlot;
}

export interface UserInput {
  name: string;
  email: string;
  password: string;
}

export interface EventInput {
  title: string;
  description: string;
  selectionType: "SINGLE" | "MULTIPLE";
}

export interface TimeSlotInput {
  startTime: string;
  durationInMinutes: number;
}

export interface UserCreateTokenInputModel {
  email: string;
  password: string;
}

export interface UserCreateTokenOutputModel {
  token: string;
}

export interface Problem {
  type: string;
  title: string;
  status: number;
  detail: string;
}

export interface EventDetails {
  event: Event;
  timeSlots: TimeSlot[];
  participantsBySlot: { [key: number]: Participant[] };
}
