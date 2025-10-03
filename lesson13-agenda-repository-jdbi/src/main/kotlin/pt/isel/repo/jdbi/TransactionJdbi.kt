package pt.isel.repo.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.repo.Transaction

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoEvents = RepositoryEventJdbi(handle)
    override val repoUsers = RepositoryUserJdbi(handle)
    override val repoParticipants = RepositoryParticipantJdbi(handle)
    override val repoSlots = RepositoryTimeSlotJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }
}
