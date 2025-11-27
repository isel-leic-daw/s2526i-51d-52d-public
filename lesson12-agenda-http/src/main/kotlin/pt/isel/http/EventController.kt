package pt.isel.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.isel.AuthenticatedUser
import pt.isel.agenda.Event
import pt.isel.http.model.EventDetailsOutput
import pt.isel.http.model.EventInput
import pt.isel.http.model.Problem
import pt.isel.service.Either
import pt.isel.service.EventError
import pt.isel.service.EventService
import pt.isel.service.Failure
import pt.isel.service.Success
import pt.isel.service.TimeSlotPublisher
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val publisher: TimeSlotPublisher,
) {
    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{eventId}")
    fun getEventById(
        @PathVariable eventId: Int,
    ): ResponseEntity<Event> =
        when (val event = eventService.getEventById(eventId)) {
            is Success -> ResponseEntity.ok(event.value)
            is Failure -> ResponseEntity.notFound().build()
        }

    @GetMapping("/{eventId}/listen")
    fun listen(
        @PathVariable eventId: Int,
    ): SseEmitter {
        val sseEmitter = SseEmitter(TimeUnit.HOURS.toMillis(1))
        publisher.addEmitter(
            eventId,
            SseUpdatedTimeSlotEmitterAdapter(
                sseEmitter,
            ),
        )
        return sseEmitter
    }

    /**
     * Get complete event details including time slots and participants
     * This endpoint reduces the number of HTTP requests needed from N+2 to 1
     * Try with:
     curl -X GET http://localhost:8080/api/events/1/details
     */
    @GetMapping("/{eventId}/details")
    fun getEventDetails(
        @PathVariable eventId: Int,
    ): ResponseEntity<Any> =
        when (val details = eventService.getEventDetails(eventId)) {
            is Success -> {
                val output =
                    EventDetailsOutput(
                        event = details.value.event,
                        timeSlots = details.value.timeSlots,
                        participantsBySlot = details.value.participantsBySlot,
                    )
                ResponseEntity.ok(output)
            }

            is Failure -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
        }

    /**
     * Try with:
     curl -X POST http://localhost:8080/api/events \
     -H "Authorization: Bearer n8AVqGnzbGkvDPryo8t14kWz6KfQIxygL3AH-HHMS28=" \
     -H "Content-Type: application/json" \
     -d '{
     "title": "Arrakis Sandstorm Meeting",
     "description": "Discuss plans for the Fremen alliance",
     "selectionType": "SINGLE"
     }'
     */
    @PostMapping
    fun createEvent(
        organizer: AuthenticatedUser,
        @RequestBody ev: EventInput,
    ): ResponseEntity<Any> {
        val event: Either<EventError.UserNotFound, Event> =
            eventService.createEvent(ev.title, ev.description, organizer.user.id, ev.selectionType)
        return when (event) {
            is Success -> ResponseEntity.ok(event.value.id)
            is Failure -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
        }
    }
}
