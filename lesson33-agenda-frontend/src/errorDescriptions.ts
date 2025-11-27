// Error descriptions mapping from /docs/problems
export const errorDescriptions: Record<string, string> = {
  "email-already-in-use":
    "There is already a Participant with given email address.",
  "event-not-found": "The specified event was not found in the system.",
  "insecure-password": "Password must have more than 4 chars.",
  "invalid-request-content":
    "The request content is invalid or malformed. Please check the request body and ensure all required fields are provided with valid values.",
  "participant-not-found":
    "The specified participant was not found in the system.",
  "timeslot-already-allocated":
    "This time slot is already allocated to another participant and cannot accept additional participants.",
  "timeslot-not-found": "The specified time slot was not found in the system.",
  "timeslot-single-has-not-multiple-participants":
    "This operation cannot be performed on a single selection time slot as it does not support multiple participants. Single selection time slots can only have one owner.",
  "user-is-already-participant-in-time-slot":
    "The user is already a participant in this time slot and cannot join again.",
  "user-is-not-organizer":
    "Only the organizer of an event can perform this action. You do not have permission to modify this event.",
  "user-is-not-participant-in-time-slot":
    "The user is not a participant in this time slot and cannot leave it. You can only leave time slots that you have previously joined.",
  "user-or-password-are-invalid":
    "The provided email or password is invalid. Please check your credentials and try again.",
};

export function getErrorDescription(errorType: string): string {
  return errorDescriptions[errorType] || errorType;
}
