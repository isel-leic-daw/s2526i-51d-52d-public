package pt.isel.service

import java.time.Instant

sealed interface UpdatedTimeSlot {
    data class Message(
        val id: Long,
        val data: TimeSlotData,
    ) : UpdatedTimeSlot

    data class KeepAlive(
        val timestamp: Instant,
    ) : UpdatedTimeSlot {
        companion object {
            var count = 1
        }

        val count: Int = Companion.count++

        override fun toString() = "${timestamp.epochSecond} - $count"
    }
}

enum class ActionKind {
    UserJoined,
    UserLeft,
}

data class TimeSlotData(
    val eventId: Int,
    val slotId: Int,
    val action: ActionKind,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val participantId: Int? = null,
)
