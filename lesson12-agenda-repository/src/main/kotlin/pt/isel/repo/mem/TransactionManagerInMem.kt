package pt.isel.repo.mem

import pt.isel.repo.Transaction
import pt.isel.repo.TransactionManager

class TransactionManagerInMem : TransactionManager {
    private val repoEvents = RepositoryEventInMem()
    private val repoUsers = RepositoryUserInMem()
    private val repoParticipants = RepositoryParticipantInMem()
    private val repoSlots = RepositoryTimeslotInMem()

    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(repoEvents, repoUsers, repoParticipants, repoSlots))
}
