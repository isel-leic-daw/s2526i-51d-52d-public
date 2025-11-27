package pt.isel.http.model

import pt.isel.agenda.Event
import pt.isel.agenda.Participant
import pt.isel.agenda.TimeSlot

/**
 * Output model for event details that includes the event, time slots, and participants
 * This reduces the number of HTTP requests needed to load event details
 */
data class EventDetailsOutput(
    val event: Event,
    val timeSlots: List<TimeSlot>,
    val participantsBySlot: Map<Int, List<Participant>>,
)
