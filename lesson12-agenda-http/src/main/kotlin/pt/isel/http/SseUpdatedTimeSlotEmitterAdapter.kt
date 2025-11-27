package pt.isel.http

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.isel.service.UpdatedTimeSlot
import pt.isel.service.UpdatedTimeSlotEmitter

/*
 * - SseEmitter - Spring MVC type
 * - UpdatedTimeSlotEmitter is our own interface (domain)
 * - SseUpdatedTimeSlotEmitterAdapter is our own type (http) that is adapted to the UpdatedTimeSlotEmitter interface,
 * -  which uses SseEmitter
 * Yuml class diagram:
   [TimeSlotController]->1[EventService]
   [TimeSlotController]-.-new>[SseUpdatedTimeSlotEmitterAdapter]
   [TimeSlotController]-.-new>[SseEmitter]
   [SseUpdatedTimeSlotEmitterAdapter]-.-new>[SseEventBuilder]
   [EventService]->*[UpdatedTimeSlotEmitter]
   [Message]-^[UpdatedTimeSlot]
   [KeepAlive]-^[UpdatedTimeSlot]
   [EventService]-.-new>[Message]
   [EventService]-.-new>[KeepAlive]
   [SseUpdatedTimeSlotEmitterAdapter]-^[UpdatedTimeSlotEmitter]

   [UpdatedTimeSlot]
   [Message|id: Long;slot: TimeSlot]
   [KeepAlive|timestamp: Instant]
   [UpdatedTimeSlotEmitter|emit(signal: UpdatedTimeSlot);onCompletion(callback: () -\> Unit);onError(callback: (Throwable) -\> Unit)]
   [EventService|addEmitter(eventId: Int, emitter: UpdatedTimeSlotEmitter);removeEmitter(eventId: Int, emitter: UpdatedTimeSlotEmitter);sendEventToAll(ev: Event, signal: UpdatedTimeSlot)]
 */
class SseUpdatedTimeSlotEmitterAdapter(
    private val sseEmitter: SseEmitter,
) : UpdatedTimeSlotEmitter {
    override fun emit(signal: UpdatedTimeSlot) {
        val msg =
            when (signal) {
                is UpdatedTimeSlot.Message ->
                    SseEmitter
                        .event()
                        .id(signal.id.toString())
                        .name("message")
                        .data(signal)

                is UpdatedTimeSlot.KeepAlive -> SseEmitter.event().comment(signal.toString())
            }
        sseEmitter.send(msg)
    }

    override fun onCompletion(callback: () -> Unit) {
        sseEmitter.onCompletion(callback)
    }

    override fun onError(callback: (Throwable) -> Unit) {
        sseEmitter.onError(callback)
    }
}
