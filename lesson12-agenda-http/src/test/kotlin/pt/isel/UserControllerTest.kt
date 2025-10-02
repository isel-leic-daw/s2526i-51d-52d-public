package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.http.UserController
import pt.isel.http.model.UserCreateTokenInputModel
import pt.isel.http.model.UserCreateTokenOutputModel
import pt.isel.http.model.UserHomeOutputModel
import pt.isel.http.model.UserInput
import pt.isel.repo.TransactionManager
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringJUnitConfig(TestConfig::class)
class UserControllerTest {
    @Autowired
    private lateinit var controllerUser: UserController

    @Autowired
    private lateinit var trxManager: TransactionManager

    @BeforeEach
    fun cleanup() {
        trxManager.run {
            repoParticipants.clear()
            repoSlots.clear()
            repoEvents.clear()
            repoUsers.clear()
        }
    }

    @Test
    fun `can create an user, obtain a token, and access user home, and logout`() {
        // given: a user
        val name = "John Rambo"
        val email = "john@rambo.vcom"
        val password = "badGuy"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        val userId =
            controllerUser.createUser(UserInput(name, email, password)).let { resp ->
                assertEquals(HttpStatus.CREATED, resp.statusCode)
                val location = resp.headers.getFirst(HttpHeaders.LOCATION)
                assertNotNull(location)
                assertTrue(location.startsWith("/api/users"))
                location.split("/").last().toInt()
            }

        // when: creating a token
        // then: the response is a 200
        val token =
            controllerUser.token(UserCreateTokenInputModel(email, password)).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode)
                assertIs<UserCreateTokenOutputModel>(resp.body)
                (resp.body as UserCreateTokenOutputModel).token
            }

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        val user = User(userId, name, email, PasswordValidationInfo(password))
        controllerUser.userHome(AuthenticatedUser(user, token)).also { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
            assertEquals(UserHomeOutputModel(userId, name, email), resp.body)
        }
    }
}
