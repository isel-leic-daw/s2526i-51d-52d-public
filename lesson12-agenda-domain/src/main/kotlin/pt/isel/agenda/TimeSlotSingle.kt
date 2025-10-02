package pt.isel.agenda

import pt.isel.User
import java.time.LocalDateTime

/**
 * Represents a time slot available for a single participant,
 * which is the owner of this time slot.
 */
class TimeSlotSingle(
    id: Int,
    startTime: LocalDateTime,
    durationInMinutes: Int,
    event: Event,
    // may be null depending on whether is selected, or not
    val owner: User? = null,
) : TimeSlot(
        id,
        startTime,
        durationInMinutes,
        event,
    ) {
    /**
     * Assign new owner if the slot is empty
     */
    fun addOwner(owner: User): TimeSlotSingle {
        check(this.owner == null) { "This time slot is already allocated to a participant." }
        return this.copy(owner = owner)
    }

    private fun copy(owner: User): TimeSlotSingle = TimeSlotSingle(id, startTime, durationInMinutes, event, owner)

    fun removeOwner(owner: User): TimeSlotSingle {
        check(this.owner != null) { "The participant ${owner.name} is not the owner of this slot and cannot be removed!" }
        require(this.owner == owner) { }
        return TimeSlotSingle(id, startTime, durationInMinutes, event, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TimeSlotSingle

        return owner == other.owner
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (owner?.hashCode() ?: 0)
        return result
    }
}
