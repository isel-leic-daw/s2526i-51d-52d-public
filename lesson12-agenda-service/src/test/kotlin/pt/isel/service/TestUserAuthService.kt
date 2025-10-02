package pt.isel.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringJUnitConfig(TestConfig::class)
class TestUserAuthService {
    @Autowired
    private lateinit var service: UserAuthService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `createUser stores user and encodes password`() {
        val user =
            service.createUser("Alice", "alice@isel.pt", "password123").let {
                check(it is Success)
                it.value
            }
        assertEquals("Alice", user.name)
        assertEquals("alice@isel.pt", user.email)
        assertTrue(passwordEncoder.matches("password123", user.passwordValidation.validationInfo))
    }

    @Test
    fun `createToken returns token for valid credentials`() {
        service.createUser("Bob", "bob@isel.pt", "secret")
        val tokenInfo =
            service.createToken("bob@isel.pt", "secret").let {
                check(it is Success)
                it.value
            }
        assertTrue(tokenInfo.tokenValue.isNotEmpty())
        assertTrue(tokenInfo.tokenExpiration.isAfter(Instant.now()))
    }

    @Test
    fun `getUserByToken returns user for valid token`() {
        val user =
            service.createUser("Carol", "carol@isel.pt", "pass123").let {
                check(it is Success)
                it.value
            }
        val tokenInfo =
            service.createToken("carol@isel.pt", "pass123").let {
                check(it is Success)
                it.value
            }
        val found = service.getUserByToken(tokenInfo.tokenValue)
        assertNotNull(found)
        assertEquals(user.id, found.id)
    }

    @Test
    fun `revokeToken removes token`() {
        service.createUser("Dave", "dave@isel.pt", "pw123")
        val tokenInfo =
            service.createToken("dave@isel.pt", "pw123").let {
                check(it is Success)
                it.value
            }
        val revoked = service.revokeToken(tokenInfo.tokenValue)
        assertTrue(revoked)
        val found = service.getUserByToken(tokenInfo.tokenValue)
        assertNull(found)
    }

    @Test
    fun `createToken throws for invalid password`() {
        service.createUser("Eve", "eve@isel.pt", "pw1")
        service.createToken("eve@isel.pt", "wrongpw").also {
            assertInstanceOf<Either.Left<*>>(it)
            assertInstanceOf<TokenCreationError.UserOrPasswordAreInvalid>(it.value)
        }
    }

    @Test
    fun `createToken throws for non-existent email`() {
        service.createToken("notfound@isel.pt", "pw").also {
            assertInstanceOf<Either.Left<*>>(it)
            assertInstanceOf<TokenCreationError.UserOrPasswordAreInvalid>(it.value)
        }
    }

    @Test
    fun `getUserByToken returns null for invalid token`() {
        val result = service.getUserByToken("invalidtoken")
        assertNull(result)
    }
}
