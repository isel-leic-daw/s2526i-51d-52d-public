package pt.isel

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.repo.jdbi.TransactionManagerJdbi
import pt.isel.repo.jdbi.configureWithAppRequirements
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RepositoryJdbiUserTest {
    companion object {
        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        val url = Environment.getDbUrl()
                        setURL(url)
                    },
                ).configureWithAppRequirements()
        val trxManager = TransactionManagerJdbi(jdbi)
    }

    @BeforeEach
    fun setup() {
        trxManager.run {
            repoSlots.clear()
            repoEvents.clear()
            repoUsers.clear()
            repoParticipants.clear()
        }
    }

    @Test
    fun `createUser and findById`() {
        trxManager.run {
            val user = repoUsers.createUser("Alice", "alice@isel.pt", PasswordValidationInfo("hash"))
            val found = repoUsers.findById(user.id)
            assertEquals(user, found)
        }
    }

    @Test
    fun `createUser and update its name and email and check changes`() {
        trxManager.run {
            val user = repoUsers.createUser("Alice", "alice@isel.pt", PasswordValidationInfo("hash"))
            val found = repoUsers.findById(user.id)
            assertEquals(user, found)
            val updatedUser = user.copy(name = "Alice Updated", email = "updated@land.com")
            repoUsers.save(updatedUser)
            val foundUpdated = repoUsers.findById(user.id)
            assertEquals(updatedUser, foundUpdated)
        }
    }

    @Test
    fun `findByEmail returns correct user`() {
        trxManager.run {
            val user = repoUsers.createUser("Bob", "bob@isel.pt", PasswordValidationInfo("hash2"))
            val found = repoUsers.findByEmail("bob@isel.pt")
            assertEquals(user, found)
            assertNull(repoUsers.findByEmail("notfound@isel.pt"))
        }
    }

    @Test
    fun `createToken and getTokenByTokenValidationInfo`() {
        trxManager.run {
            val user = repoUsers.createUser("Carol", "carol@isel.pt", PasswordValidationInfo("hash3"))
            val tokenValidationInfo = TokenValidationInfo("token123")
            val now = Instant.now().truncatedTo(SECONDS)
            val token = Token(tokenValidationInfo, user.id, now, now)
            repoUsers.createToken(token, maxTokens = 2)
            val result = repoUsers.getTokenByTokenValidationInfo(tokenValidationInfo)
            assertNotNull(result)
            assertEquals(user, result.first)
            assertEquals(token, result.second)
        }
    }

    @Test
    fun `createToken removes oldest when maxTokens exceeded`() {
        trxManager.run {
            val user = repoUsers.createUser("Dave", "dave@isel.pt", PasswordValidationInfo("hash4"))
            val init = Instant.now().minusSeconds(60)
            val t1 = Token(TokenValidationInfo("t1"), user.id, init, Instant.now().minusSeconds(10))
            val t2 = Token(TokenValidationInfo("t2"), user.id, init, Instant.now().minusSeconds(5))
            val t3 = Token(TokenValidationInfo("t3"), user.id, init, Instant.now())
            repoUsers.createToken(t1, maxTokens = 2)
            repoUsers.createToken(t2, maxTokens = 2)
            repoUsers.createToken(t3, maxTokens = 2)
            // t1 should be removed
            assertNull(repoUsers.getTokenByTokenValidationInfo(TokenValidationInfo("t1")))
            assertNotNull(repoUsers.getTokenByTokenValidationInfo(TokenValidationInfo("t2")))
            assertNotNull(repoUsers.getTokenByTokenValidationInfo(TokenValidationInfo("t3")))
        }
    }

    @Test
    fun `updateTokenLastUsed replaces token`() {
        trxManager.run {
            val user = repoUsers.createUser("Eve", "eve@isel.pt", PasswordValidationInfo("hash5"))
            val info = TokenValidationInfo("tokenEve")
            val init = Instant.now().truncatedTo(SECONDS).minusSeconds(200)
            val tokenOld = Token(info, user.id, init, init.plusSeconds(100))
            repoUsers.createToken(tokenOld, maxTokens = 2)
            val tokenNew = Token(info, user.id, init, Instant.now().truncatedTo(SECONDS))
            repoUsers.updateTokenLastUsed(tokenNew, tokenNew.lastUsedAt)
            val result: Pair<User, Token>? = repoUsers.getTokenByTokenValidationInfo(info)
            assertNotNull(result)
            assertEquals(tokenNew, result.second)
        }
    }

    @Test
    fun `removeTokenByValidationInfo removes token`() {
        trxManager.run {
            val user = repoUsers.createUser("Frank", "frank@isel.pt", PasswordValidationInfo("hash6"))
            val info = TokenValidationInfo("tokenFrank")
            val token = Token(info, user.id, Instant.now(), Instant.now())
            repoUsers.createToken(token, maxTokens = 2)
            val removed = repoUsers.removeTokenByValidationInfo(info)
            assertEquals(1, removed)
            assertNull(repoUsers.getTokenByTokenValidationInfo(info))
        }
    }
}
