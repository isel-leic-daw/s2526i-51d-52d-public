package pt.isel.agenda

import java.time.LocalDateTime

/**
 * Represents a time slot available for multiple participants
 */
class TimeSlotMultiple(
    id: Int,
    startTime: LocalDateTime,
    durationInMinutes: Int,
    event: Event,
) : TimeSlot(
        id,
        startTime,
        durationInMinutes,
        event,
    )
