package pt.isel.repo

import pt.isel.User
import pt.isel.agenda.Participant
import pt.isel.agenda.TimeSlotMultiple

/**
 * Repository interface for managing participants, extends the generic Repository
 */
interface RepositoryParticipant : Repository<Participant> {
    /**
     * For a Participant in a TimeSlotSingle then this slot argument is null.
     */
    fun createParticipant(
        user: User,
        slot: TimeSlotMultiple,
    ): Participant

    fun findByEmail(
        email: String,
        slot: TimeSlotMultiple,
    ): Participant?

    fun findAllByTimeSlot(slot: TimeSlotMultiple): List<Participant>
}
