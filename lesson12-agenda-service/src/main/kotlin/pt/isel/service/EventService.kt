package pt.isel.service

import jakarta.inject.Named
import pt.isel.agenda.Event
import pt.isel.agenda.Participant
import pt.isel.agenda.SelectionType
import pt.isel.agenda.TimeSlot
import pt.isel.agenda.TimeSlotMultiple
import pt.isel.agenda.TimeSlotSingle
import pt.isel.repo.TransactionManager
import java.time.LocalDateTime

sealed class EventError {
    data object EventNotFound : EventError()

    data object UserNotFound : EventError()

    data object TimeSlotNotFound : EventError()

    data object SingleTimeSlotAlreadyAllocated : EventError()

    data object UserIsAlreadyParticipantInTimeSlot : EventError()

    data object UserIsNotOrganizer : EventError()

    data object UserIsNotParticipantInTimeSlot : EventError()
}

sealed class TimeSlotError {
    data object TimeSlotNotFound : TimeSlotError()

    data object TimeSlotSingleHasNotMultipleParticipants : TimeSlotError()
}

/**
 * Data class that holds all information needed to display event details
 * This reduces the number of queries needed
 */
data class EventDetails(
    val event: Event,
    val timeSlots: List<TimeSlot>,
    val participantsBySlot: Map<Int, List<Participant>>,
)

@Named
class EventService(
    private val trxManager: TransactionManager,
    private val publisher: TimeSlotPublisher,
) {
    /**
     * Add participant to a time slot
     */
    fun addParticipantToTimeSlot(
        timeSlotId: Int,
        userId: Int,
    ): Either<EventError, TimeSlot> =
        trxManager.run {
            // Find the time slot within the event
            val timeSlot: TimeSlot = repoSlots.findById(timeSlotId) ?: return@run failure(EventError.TimeSlotNotFound)

            // Fetch the User
            val user = repoUsers.findById(userId) ?: return@run failure(EventError.UserNotFound)

            when (timeSlot) {
                is TimeSlotSingle -> {
                    // A TimeSlotSingle with already an owner cannot be allocated to other participant
                    if (timeSlot.owner != null) {
                        return@run failure(EventError.SingleTimeSlotAlreadyAllocated)
                    }
                    // Try to add the participant to the time slot
                    val updatedTimeSlot = timeSlot.addOwner(user)

                    // Replace the old time slot with the updated one in the event
                    repoSlots.save(updatedTimeSlot)
                    publisher.sendMessageToAll(timeSlot.event, updatedTimeSlot, user, ActionKind.UserJoined)

                    // Return the updated event in the Either type
                    success(updatedTimeSlot)
                }

                is TimeSlotMultiple -> {
                    // Return Failure if the user is already a participant in that TimeSlot
                    if (repoParticipants.findByEmail(user.email, timeSlot) != null) {
                        return@run failure(EventError.UserIsAlreadyParticipantInTimeSlot)
                    }
                    // Otherwise, create a new Participant in that TimeSlot for that user.
                    val participant = repoParticipants.createParticipant(user, timeSlot)
                    publisher.sendMessageToAll(
                        timeSlot.event,
                        timeSlot,
                        participant.user,
                        ActionKind.UserJoined,
                        participant,
                    )
                    success(timeSlot)
                }
            }
        }

    /**
     * Create a free time slot based on event's selection type
     * Only the organizer of the event can create time slots
     */
    fun createFreeTimeSlot(
        eventId: Int,
        userId: Int,
        startTime: LocalDateTime,
        durationInMinutes: Int,
    ): Either<EventError, TimeSlot> =
        trxManager.run {
            val event = repoEvents.findById(eventId) ?: return@run failure(EventError.EventNotFound)

            // Check if the user is the organizer of the event
            if (event.organizer.id != userId) {
                return@run failure(EventError.UserIsNotOrganizer)
            }

            // Determine TimeSlot type based on Event's selection type and create it on Repository
            val timeSlot =
                when (event.selectionType) {
                    SelectionType.SINGLE -> repoSlots.createTimeSlotSingle(startTime, durationInMinutes, event)
                    SelectionType.MULTIPLE -> repoSlots.createTimeSlotMultiple(startTime, durationInMinutes, event)
                }
            return@run success(timeSlot)
        }

    fun getAllEvents(): List<Event> = trxManager.run { repoEvents.findAll() }

    fun getEventById(eventId: Int): Either<EventError.EventNotFound, Event> =
        trxManager.run {
            repoEvents.findById(eventId)?.let { success(it) } ?: failure(EventError.EventNotFound)
        }

    fun getEventTimeSlots(eventId: Int): List<TimeSlot> =
        trxManager.run {
            val event = repoEvents.findById(eventId) ?: return@run emptyList()
            repoSlots.findAllByEvent(event)
        }

    fun createEvent(
        title: String,
        description: String?,
        organizerId: Int,
        selectionType: SelectionType,
    ): Either<EventError.UserNotFound, Event> =
        trxManager.run {
            val organizer = repoUsers.findById(organizerId) ?: return@run failure(EventError.UserNotFound)
            success(repoEvents.createEvent(title, description, organizer, selectionType))
        }

    fun getParticipantsInTimeSlot(timeSlotId: Int): Either<TimeSlotError, List<Participant>> =
        trxManager.run {
            val slot = repoSlots.findById(timeSlotId) ?: return@run failure(TimeSlotError.TimeSlotNotFound)
            when (slot) {
                is TimeSlotSingle -> failure(TimeSlotError.TimeSlotSingleHasNotMultipleParticipants)
                is TimeSlotMultiple -> success(repoParticipants.findAllByTimeSlot(slot))
            }
        }

    /**
     * Remove participant from a time slot
     */
    fun removeParticipantFromTimeSlot(
        timeSlotId: Int,
        userId: Int,
    ): Either<EventError, TimeSlot> =
        trxManager.run {
            // Find the time slot
            val timeSlot: TimeSlot = repoSlots.findById(timeSlotId) ?: return@run failure(EventError.TimeSlotNotFound)

            // Fetch the User
            val user = repoUsers.findById(userId) ?: return@run failure(EventError.UserNotFound)

            when (timeSlot) {
                is TimeSlotSingle -> {
                    // Check if the user is the owner
                    if (timeSlot.owner?.id != userId) {
                        return@run failure(EventError.UserIsNotParticipantInTimeSlot)
                    }
                    // Remove the owner
                    val updatedTimeSlot = timeSlot.removeOwner(user)
                    repoSlots.save(updatedTimeSlot)
                    publisher.sendMessageToAll(timeSlot.event, updatedTimeSlot, user, ActionKind.UserLeft)
                    success(updatedTimeSlot)
                }

                is TimeSlotMultiple -> {
                    // Find the participant
                    val participant =
                        repoParticipants.findByEmail(user.email, timeSlot)
                            ?: return@run failure(EventError.UserIsNotParticipantInTimeSlot)

                    // Delete the participant
                    repoParticipants.deleteById(participant.id)
                    publisher.sendMessageToAll(timeSlot.event, timeSlot, user, ActionKind.UserLeft, participant)
                    success(timeSlot)
                }
            }
        }

    /**
     * Get complete event details including time slots and participants
     * This method fetches all required data in a single transaction
     */
    fun getEventDetails(eventId: Int): Either<EventError.EventNotFound, EventDetails> =
        trxManager.run {
            val event = repoEvents.findById(eventId) ?: return@run failure(EventError.EventNotFound)
            val timeSlots = repoSlots.findAllByEvent(event)

            // Fetch participants only for MULTIPLE selection type events
            val participantsBySlot =
                if (event.selectionType == SelectionType.MULTIPLE) {
                    timeSlots
                        .filterIsInstance<TimeSlotMultiple>()
                        .associate { slot ->
                            slot.id to repoParticipants.findAllByTimeSlot(slot)
                        }
                } else {
                    emptyMap()
                }

            success(EventDetails(event, timeSlots, participantsBySlot))
        }
}
