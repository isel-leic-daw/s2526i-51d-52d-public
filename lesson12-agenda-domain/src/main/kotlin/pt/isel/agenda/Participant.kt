package pt.isel.agenda

import pt.isel.User

/**
 * Represents a participant in a TimeSlotMultiple
 */
data class Participant(
    val id: Int,
    val user: User,
    val slot: TimeSlotMultiple,
)
