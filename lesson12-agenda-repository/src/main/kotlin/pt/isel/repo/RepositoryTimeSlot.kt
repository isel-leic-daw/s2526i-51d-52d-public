package pt.isel.repo

import pt.isel.agenda.Event
import pt.isel.agenda.TimeSlot
import pt.isel.agenda.TimeSlotMultiple
import pt.isel.agenda.TimeSlotSingle
import java.time.LocalDateTime

/**
 * Repository interface for managing time slots, extends the generic Repository
 */
interface RepositoryTimeSlot : Repository<TimeSlot> {
    fun createTimeSlotSingle(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotSingle

    fun createTimeSlotMultiple(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotMultiple

    fun findAllByEvent(event: Event): List<TimeSlot>
}
