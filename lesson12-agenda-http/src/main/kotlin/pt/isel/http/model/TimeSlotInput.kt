package pt.isel.http.model

import java.time.LocalDateTime

data class TimeSlotInput(
    val startTime: LocalDateTime,
    val durationInMinutes: Int,
)
