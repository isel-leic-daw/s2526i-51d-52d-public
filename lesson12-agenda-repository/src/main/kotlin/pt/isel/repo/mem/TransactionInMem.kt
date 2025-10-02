package pt.isel.repo.mem

import pt.isel.repo.RepositoryEvent
import pt.isel.repo.RepositoryParticipant
import pt.isel.repo.RepositoryTimeSlot
import pt.isel.repo.RepositoryUser
import pt.isel.repo.Transaction

class TransactionInMem(
    override val repoEvents: RepositoryEvent,
    override val repoUsers: RepositoryUser,
    override val repoParticipants: RepositoryParticipant,
    override val repoSlots: RepositoryTimeSlot,
) : Transaction {
    override fun rollback(): Unit = throw UnsupportedOperationException()
}
