package pt.isel.repo.mem

import pt.isel.User
import pt.isel.agenda.Participant
import pt.isel.agenda.TimeSlotMultiple
import pt.isel.repo.RepositoryParticipant

/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
class RepositoryParticipantInMem : RepositoryParticipant {
    private val participants = mutableListOf<Participant>()

    override fun createParticipant(
        user: User,
        slot: TimeSlotMultiple,
    ): Participant =
        Participant(participants.count(), user, slot)
            .also { participants.add(it) }

    override fun findByEmail(
        email: String,
        slot: TimeSlotMultiple,
    ): Participant? =
        participants.firstOrNull {
            it.user.email == email && it.slot.id == slot.id
        }

    override fun findAllByTimeSlot(slot: TimeSlotMultiple): List<Participant> = participants.filter { it.slot.id == slot.id }

    override fun findById(id: Int): Participant? = participants.firstOrNull { it.id == id }

    override fun findAll(): List<Participant> = participants.toList()

    override fun save(entity: Participant) {
        participants.removeIf { it.id == entity.id }
        participants.add(entity)
    }

    override fun deleteById(id: Int) {
        participants.removeIf { it.id == id }
    }

    override fun clear() {
        participants.clear()
    }
}
