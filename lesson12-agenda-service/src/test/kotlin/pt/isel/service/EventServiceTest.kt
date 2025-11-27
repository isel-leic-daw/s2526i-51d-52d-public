package pt.isel.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.User
import pt.isel.agenda.Event
import pt.isel.agenda.Participant
import pt.isel.agenda.SelectionType
import pt.isel.agenda.TimeSlot
import pt.isel.agenda.TimeSlotMultiple
import pt.isel.agenda.TimeSlotSingle
import pt.isel.repo.TransactionManager
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringJUnitConfig(TestConfig::class)
class EventServiceTest {
    @Autowired
    private lateinit var serviceEvent: EventService

    @Autowired
    private lateinit var serviceUser: UserAuthService

    @Autowired
    private lateinit var trxManager: TransactionManager

    @BeforeEach
    fun setup() {
        trxManager.run { repoUsers.clear() }
    }

    @Test
    fun `addParticipantToTimeSlot should add participant to a time slot`() {
        val organizer = serviceUser.createUser("John", "john@example.com", "camafeuAtleta")
        assertIs<Success<User>>(organizer)

        val ev =
            serviceEvent
                .createEvent("Meeting", null, organizer.value.id, SelectionType.MULTIPLE)
                .let { it as Success<Event> }
        val ts = serviceEvent.createFreeTimeSlot(ev.value.id, organizer.value.id, LocalDateTime.now(), 60)
        assertIs<Success<TimeSlot>>(ts)
        val timeSlotId = ts.value.id

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, organizer.value.id)

        assertTrue(updatedTimeSlot is Success)
        assertIs<TimeSlotMultiple>(updatedTimeSlot.value)

        val participants = serviceEvent.getParticipantsInTimeSlot(timeSlotId)
        assertIs<Success<List<Participant>>>(participants)
        assertEquals(1, participants.value.size)
        assertEquals(organizer.value, participants.value[0].user)
    }

    @Test
    fun `addParticipantToTimeSlot should return Error already allocated for a Single Time slot with an owner`() {
        val organizer =
            serviceUser
                .createUser("John", "john@example.com", "janitaSalome")
                .let {
                    check(it is Success)
                    it.value
                }
        assertIs<User>(organizer)
        val ts =
            serviceEvent
                .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
                .let {
                    check(it is Success)
                    serviceEvent.createFreeTimeSlot(it.value.id, organizer.id, LocalDateTime.now(), 60)
                }
        assertIs<Success<TimeSlot>>(ts)

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(ts.value.id, organizer.id)

        assertIs<Success<TimeSlot>>(updatedTimeSlot)
        assertIs<TimeSlotSingle>(updatedTimeSlot.value)
        val owner = (updatedTimeSlot.value as TimeSlotSingle).owner
        assertNotNull(owner)
        owner.run {
            assertEquals(id, organizer.id)
            assertEquals(name, organizer.name)
            assertEquals(email, organizer.email)
        }
        val otherUser = serviceUser.createUser("john", "john@rambo.com", "camafeuAtleta")
        assertIs<Success<User>>(otherUser)
        val res = serviceEvent.addParticipantToTimeSlot(ts.value.id, otherUser.value.id)
        assertIs<Failure<EventError>>(res)
        assertIs<EventError.SingleTimeSlotAlreadyAllocated>(res.value)
    }

    @Test
    fun `addParticipantToTimeSlot should return UserNotFound when participant is not found`() {
        val participant =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }

        val ts =
            serviceEvent
                .createEvent(
                    "Meeting",
                    null,
                    participant.id,
                    SelectionType.MULTIPLE,
                ).let {
                    check(it is Success)
                    it.value
                }.let { event -> serviceEvent.createFreeTimeSlot(event.id, participant.id, LocalDateTime.now(), 60) }
        assertIs<Success<TimeSlot>>(ts)

        // Try to add unknown participant
        val result = serviceEvent.addParticipantToTimeSlot(ts.value.id, -9999)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.UserNotFound)
    }

    @Test
    fun `createUser should create and return a participant`() {
        val name = "Alice"
        val email = "alice@example.com"
        val pass = "camafeuAtleta"

        val result = serviceUser.createUser(name, email, pass)

        assertIs<Success<User>>(result)
        assertEquals(name, result.value.name)
        assertEquals(email, result.value.email)
        assertTrue { serviceUser.validatePassword(pass, result.value.passwordValidation) }
    }

    @Test
    fun `createUser with already used email should return an error`() {
        serviceUser.createUser("Alice", "alice@example.com", "camafeuAtleta")

        val result: Either<UserError, User> =
            serviceUser.createUser("Mary", "alice@example.com", "janitaSalome")

        assertIs<Failure<UserError>>(result)
        assertIs<UserError>(result.value)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`() {
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }
        val eventId =
            serviceEvent
                .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
                .let {
                    check(it is Success)
                    it.value.id
                }

        val result = serviceEvent.createFreeTimeSlot(eventId, organizer.id, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event =
            serviceEvent.getEventById(eventId).let {
                check(it is Success)
                it.value
            }
        val expected = TimeSlotSingle(result.value.id, startTime, durationInMinutes, event, null)
        assertEquals(expected, result.value)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type MULTIPLE`() {
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }
        val eventId =
            serviceEvent
                .createEvent("Meeting", null, organizer.id, SelectionType.MULTIPLE)
                .let {
                    check(it is Success)
                    it.value.id
                }

        val result = serviceEvent.createFreeTimeSlot(eventId, organizer.id, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event =
            serviceEvent.getEventById(eventId).let {
                check(it is Success)
                it.value
            }
        val expected = TimeSlotMultiple(result.value.id, startTime, durationInMinutes, event)
        assertEquals(expected, result.value)
    }

    @Test
    fun `createFreeTimeSlot should return EventNotFound when event is not found`() {
        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60

        val result = serviceEvent.createFreeTimeSlot(eventId, 0, startTime, durationInMinutes)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }
}
