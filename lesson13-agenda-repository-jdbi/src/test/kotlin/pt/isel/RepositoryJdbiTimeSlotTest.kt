package pt.isel

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.agenda.SelectionType
import pt.isel.agenda.TimeSlotSingle
import pt.isel.repo.jdbi.TransactionManagerJdbi
import pt.isel.repo.jdbi.configureWithAppRequirements
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertEquals

private fun newRandomPassword() = "token-${abs(Random.nextLong())}"

class RepositoryJdbiTimeSlotTest {
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
    fun clean() {
        trxManager.run {
            repoSlots.clear()
            repoEvents.clear()
            repoUsers.clear()
            repoParticipants.clear()
        }
    }

    @Test
    fun `test create event and find it`() =
        trxManager.run {
            val alice =
                repoUsers.createUser(
                    "Alice",
                    "alice@example.com",
                    PasswordValidationInfo(newRandomPassword()),
                )
            val repoEvents =
                repoEvents.also {
                    it.createEvent(
                        title = "Team Meeting",
                        description = "Discuss project updates",
                        organizer = alice,
                        selectionType = SelectionType.SINGLE,
                    )
                }
            val events = repoEvents.findAll()
            assertEquals(1, events.size)
        }

    @Test
    fun `test replacing a time slot in an event`() =
        trxManager.run {
            val repoUsers =
                repoUsers.also {
                    it.createUser(
                        "Alice",
                        "alice@example.com",
                        PasswordValidationInfo(newRandomPassword()),
                    )
                }
            assertEquals(0, repoSlots.findAll().size)

            val repoEvents =
                repoEvents.also {
                    it.createEvent(
                        title = "Team Meeting",
                        description = "Discuss project updates",
                        organizer = repoUsers.findAll()[0],
                        selectionType = SelectionType.SINGLE,
                    )
                }

            val event = repoEvents.findAll().first()

            val slot1 =
                repoSlots.createTimeSlotSingle(
                    startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
                    durationInMinutes = 60,
                    event,
                )
            val slot2 =
                repoSlots.createTimeSlotSingle(
                    startTime = LocalDateTime.of(2024, 9, 30, 11, 0),
                    durationInMinutes = 60,
                    event,
                )
            val events = repoSlots.findAllByEvent(event)
            assertEquals(2, events.size)

            assertEquals(setOf(slot1, slot2), events.toSet())

            val newSlot = TimeSlotSingle(slot2.id, LocalDateTime.of(2024, 9, 30, 10, 30), 60, event)
            repoSlots.save(newSlot)

            val slots = repoSlots.findAllByEvent(event).toSet()

            assertEquals(2, slots.size)
            assertEquals(setOf(slot1, newSlot), repoSlots.findAllByEvent(event).toSet())
        }
}
