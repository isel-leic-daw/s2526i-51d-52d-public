package pt.isel.repo.jdbi

import org.jdbi.v3.core.Jdbi
import pt.isel.repo.Transaction
import pt.isel.repo.TransactionManager

class TransactionManagerJdbi(
    private val jdbi: Jdbi,
) : TransactionManager {
    override fun <R> run(block: Transaction.() -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = TransactionJdbi(handle)
            block(transaction)
        }
}
