package pt.isel.agenda

import java.time.LocalDateTime

sealed class TimeSlot(
    val id: Int,
    val startTime: LocalDateTime,
    val durationInMinutes: Int,
    val event: Event,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeSlot

        if (id != other.id) return false
        if (startTime != other.startTime) return false
        if (durationInMinutes != other.durationInMinutes) return false
        if (event != other.event) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + startTime.hashCode()
        result = 31 * result + durationInMinutes
        result = 31 * result + event.hashCode()
        return result
    }
}
