package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.agenda.SelectionType
import pt.isel.http.EventController
import pt.isel.repo.TransactionManager
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertEquals

fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

@SpringJUnitConfig(TestConfig::class)
class EventControllerTest {
    @Autowired
    private lateinit var controllerEvents: EventController

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
    fun `getAllEvents should return a list of events`() {
        // Arrange
        val rose =
            trxManager.run {
                repoUsers.createUser(
                    "Rose Mary",
                    "rose@example.com",
                    PasswordValidationInfo(newTokenValidationData()),
                )
            }
        trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }

        trxManager.run {
            repoEvents.createEvent(
                "Status Meeting",
                "Coffee break and more",
                rose,
                SelectionType.MULTIPLE,
            )
        }
        val resp = controllerEvents.getAllEvents()
        assertEquals(HttpStatus.OK, resp.statusCode)
        assertEquals(2, resp.body?.size)
    }
}
