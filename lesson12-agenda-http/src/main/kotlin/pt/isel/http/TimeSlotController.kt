package pt.isel.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.AuthenticatedUser
import pt.isel.agenda.TimeSlot
import pt.isel.http.model.Problem
import pt.isel.http.model.TimeSlotInput
import pt.isel.service.Either
import pt.isel.service.EventError
import pt.isel.service.EventError.EventNotFound
import pt.isel.service.EventError.SingleTimeSlotAlreadyAllocated
import pt.isel.service.EventError.TimeSlotNotFound
import pt.isel.service.EventError.UserIsAlreadyParticipantInTimeSlot
import pt.isel.service.EventError.UserNotFound
import pt.isel.service.EventService
import pt.isel.service.Failure
import pt.isel.service.Success
import pt.isel.service.TimeSlotError

@RestController
@RequestMapping("/api/events")
class TimeSlotController(
    private val eventService: EventService,
) {
    /**
     * Get all time slots for a specific event
     * Try with:
     curl -X GET http://localhost:8080/api/events/1/timeslots
     */
    @GetMapping("/{eventId}/timeslots")
    fun getEventTimeSlots(
        @PathVariable eventId: Int,
    ): ResponseEntity<List<TimeSlot>> {
        val timeSlots = eventService.getEventTimeSlots(eventId)
        return ResponseEntity.ok(timeSlots)
    }

    /**
     * Get all participants in a time slot
     * Try with:
     curl -X GET http://localhost:8080/api/events/1/timeslots/1/participants
     */
    @GetMapping("/{eventId}/timeslots/{timeSlotId}/participants")
    fun getParticipantsInTimeSlot(
        @PathVariable eventId: Int,
        @PathVariable timeSlotId: Int,
    ): ResponseEntity<Any> {
        val participants = eventService.getParticipantsInTimeSlot(timeSlotId)
        return when (participants) {
            is Success -> ResponseEntity.ok(participants.value)
            is Failure ->
                when (participants.value) {
                    TimeSlotError.TimeSlotNotFound -> Problem.TimeSlotNotFound.response(HttpStatus.NOT_FOUND)
                    TimeSlotError.TimeSlotSingleHasNotMultipleParticipants ->
                        Problem.TimeSlotSingleHasNotMultipleParticipants.response(HttpStatus.BAD_REQUEST)
                }
        }
    }

    /**
     * Try with:
     curl -X POST http://localhost:8080/api/events/1/timeslots \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{
     "startTime": "2024-10-10T17:00:00",
     "durationInMinutes": 60
     }'
     */
    @PostMapping("/{eventId}/timeslots")
    fun createFreeTimeSlot(
        user: AuthenticatedUser,
        @PathVariable eventId: Int,
        @RequestBody timeSlotInput: TimeSlotInput,
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventError, TimeSlot> =
            eventService.createFreeTimeSlot(
                eventId,
                user.user.id,
                timeSlotInput.startTime,
                timeSlotInput.durationInMinutes,
            )

        return when (timeSlot) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(timeSlot.value)
            is Failure ->
                when (timeSlot.value) {
                    EventError.EventNotFound -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
                    EventError.UserIsNotOrganizer -> Problem.UserIsNotOrganizer.response(HttpStatus.FORBIDDEN)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                }
        }
    }

    /**
     * Try with:
     curl -X PUT http://localhost:8080/api/events/1/timeslots/1/participants \
     -H "Authorization: Bearer n8AVqGnzbGkvDPryo8t14kWz6KfQIxygL3AH-HHMS28=" \
     */
    @PutMapping("/{eventId}/timeslots/{timeSlotId}/participants")
    fun addParticipantToTimeSlot(
        user: AuthenticatedUser,
        @PathVariable eventId: Int,
        @PathVariable timeSlotId: Int,
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventError, TimeSlot> =
            eventService.addParticipantToTimeSlot(timeSlotId, user.user.id)

        return when (timeSlot) {
            is Success -> ResponseEntity.ok(timeSlot.value)
            is Failure ->
                when (timeSlot.value) {
                    is TimeSlotNotFound -> Problem.TimeSlotNotFound.response(HttpStatus.NOT_FOUND)
                    is UserNotFound -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
                    is EventNotFound -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
                    is SingleTimeSlotAlreadyAllocated -> Problem.TimeSlotAlreadyAllocated.response(HttpStatus.CONFLICT)
                    UserIsAlreadyParticipantInTimeSlot -> Problem.UserIsAlreadyParticipantInTimeSlot.response(HttpStatus.CONFLICT)
                    EventError.UserIsNotOrganizer -> Problem.UserIsNotOrganizer.response(HttpStatus.FORBIDDEN)
                    EventError.UserIsNotParticipantInTimeSlot -> Problem.UserIsNotParticipantInTimeSlot.response(HttpStatus.BAD_REQUEST)
                }
        }
    }

    /**
     * Try with:
     curl -X DELETE http://localhost:8080/api/events/1/timeslots/1/participants \
     -H "Authorization: Bearer n8AVqGnzbGkvDPryo8t14kWz6KfQIxygL3AH-HHMS28=" \
     */
    @DeleteMapping("/{eventId}/timeslots/{timeSlotId}/participants")
    fun removeParticipantFromTimeSlot(
        user: AuthenticatedUser,
        @PathVariable eventId: Int,
        @PathVariable timeSlotId: Int,
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventError, TimeSlot> =
            eventService.removeParticipantFromTimeSlot(timeSlotId, user.user.id)

        return when (timeSlot) {
            is Success -> ResponseEntity.ok(timeSlot.value)
            is Failure ->
                when (timeSlot.value) {
                    is TimeSlotNotFound -> Problem.TimeSlotNotFound.response(HttpStatus.NOT_FOUND)
                    is UserNotFound -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
                    is EventNotFound -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
                    EventError.UserIsNotParticipantInTimeSlot -> Problem.UserIsNotParticipantInTimeSlot.response(HttpStatus.BAD_REQUEST)
                    is SingleTimeSlotAlreadyAllocated -> Problem.TimeSlotAlreadyAllocated.response(HttpStatus.CONFLICT)
                    UserIsAlreadyParticipantInTimeSlot -> Problem.UserIsAlreadyParticipantInTimeSlot.response(HttpStatus.CONFLICT)
                    EventError.UserIsNotOrganizer -> Problem.UserIsNotOrganizer.response(HttpStatus.FORBIDDEN)
                }
        }
    }
}
