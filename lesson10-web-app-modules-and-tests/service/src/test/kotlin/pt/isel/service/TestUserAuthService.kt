package pt.isel.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.domain.Sha256TokenEncoder
import pt.isel.domain.TokenEncoder
import pt.isel.domain.TokenValidationInfo
import pt.isel.domain.UsersDomainConfig
import pt.isel.repo.mem.RepositoryUserInMem
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Configuration
@ComponentScan("pt.isel")
class WebAppTestConfig {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun repositoryUser() = RepositoryUserInMem()

    @Bean
    fun usersDomainConfig() =
        UsersDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = Duration.ofHours(24),
            tokenRollingTtl = Duration.ofHours(1),
            maxTokensPerUser = 3,
        )
}

@SpringJUnitConfig(WebAppTestConfig::class)
class TestUserAuthService {
    @Autowired
    private lateinit var service: UserAuthService

    private val config =
        UsersDomainConfig(
            maxTokensPerUser = 3,
            tokenSizeInBytes = 16,
            tokenTtl = Duration.ofHours(1),
            tokenRollingTtl = Duration.ofMinutes(30),
        )
    private val passwordEncoder = BCryptPasswordEncoder()
    private val tokenEncoder =
        object : TokenEncoder {
            override fun createValidationInformation(token: String): TokenValidationInfo = TokenValidationInfo(token)
        }
    private val clock = Clock.systemUTC()

    @Test
    fun `createUser stores user and encodes password`() {
        val user = service.createUser("Alice", "alice@isel.pt", "password123")
        assertEquals("Alice", user.name)
        assertEquals("alice@isel.pt", user.email)
        assertTrue(passwordEncoder.matches("password123", user.passwordValidation.validationInfo))
    }

    @Test
    fun `createToken returns token for valid credentials`() {
        service.createUser("Bob", "bob@isel.pt", "secret")
        val tokenInfo = service.createToken("bob@isel.pt", "secret")
        assertTrue(tokenInfo.tokenValue.isNotEmpty())
        assertTrue(tokenInfo.tokenExpiration.isAfter(Instant.now()))
    }

    @Test
    fun `getUserByToken returns user for valid token`() {
        val user = service.createUser("Carol", "carol@isel.pt", "pass")
        val tokenInfo = service.createToken("carol@isel.pt", "pass")
        val found = service.getUserByToken(tokenInfo.tokenValue)
        assertNotNull(found)
        assertEquals(user.id, found.id)
    }

    @Test
    fun `revokeToken removes token`() {
        service.createUser("Dave", "dave@isel.pt", "pw")
        val tokenInfo = service.createToken("dave@isel.pt", "pw")
        val revoked = service.revokeToken(tokenInfo.tokenValue)
        assertTrue(revoked)
        val found = service.getUserByToken(tokenInfo.tokenValue)
        assertNull(found)
    }

    @Test
    fun `createToken throws for invalid password`() {
        service.createUser("Eve", "eve@isel.pt", "pw1")
        assertFailsWith<IllegalArgumentException> {
            service.createToken("eve@isel.pt", "wrongpw")
        }
    }

    @Test
    fun `createToken throws for non-existent email`() {
        assertFailsWith<IllegalArgumentException> {
            service.createToken("notfound@isel.pt", "pw")
        }
    }

    @Test
    fun `getUserByToken returns null for invalid token`() {
        val result = service.getUserByToken("invalidtoken")
        assertNull(result)
    }
}
