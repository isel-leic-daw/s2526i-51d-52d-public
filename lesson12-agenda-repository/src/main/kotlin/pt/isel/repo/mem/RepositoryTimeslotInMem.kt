package pt.isel.repo.mem

import pt.isel.agenda.Event
import pt.isel.agenda.TimeSlot
import pt.isel.agenda.TimeSlotMultiple
import pt.isel.agenda.TimeSlotSingle
import pt.isel.repo.RepositoryTimeSlot
import java.time.LocalDateTime

/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
class RepositoryTimeslotInMem : RepositoryTimeSlot {
    private val timeSlots = mutableListOf<TimeSlot>()

    override fun createTimeSlotSingle(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotSingle =
        TimeSlotSingle(timeSlots.count(), startTime, durationInMinutes, event)
            .also { timeSlots.add(it) }

    override fun createTimeSlotMultiple(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotMultiple =
        TimeSlotMultiple(timeSlots.count(), startTime, durationInMinutes, event)
            .also { timeSlots.add(it) }

    override fun findAllByEvent(event: Event): List<TimeSlot> = timeSlots.filter { it.event.id == event.id }

    override fun findById(id: Int): TimeSlot? = timeSlots.firstOrNull { it.id == id }

    override fun findAll(): List<TimeSlot> = timeSlots.toList()

    override fun save(entity: TimeSlot) {
        timeSlots.removeIf { it.id == entity.id }
        timeSlots.add(entity)
    }

    override fun deleteById(id: Int) {
        timeSlots.removeIf { it.id == id }
    }

    override fun clear() {
        timeSlots.clear()
    }
}
