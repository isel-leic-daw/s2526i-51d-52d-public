package pt.isel.repo.mem

import pt.isel.User
import pt.isel.agenda.Event
import pt.isel.agenda.SelectionType
import pt.isel.repo.RepositoryEvent

/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
class RepositoryEventInMem : RepositoryEvent {
    private val events = mutableListOf<Event>()

    override fun createEvent(
        title: String,
        description: String?,
        organizer: User,
        selectionType: SelectionType,
    ): Event =
        Event(events.count(), title, description, organizer, selectionType)
            .also { events.add(it) }

    override fun findById(id: Int): Event? = events.firstOrNull { it.id == id }

    override fun findAll(): List<Event> = events.toList()

    override fun save(entity: Event) {
        events.removeIf { it.id == entity.id }
        events.add(entity)
    }

    override fun deleteById(id: Int) {
        events.removeIf { it.id == id }
    }

    override fun clear() {
        events.clear()
    }
}
