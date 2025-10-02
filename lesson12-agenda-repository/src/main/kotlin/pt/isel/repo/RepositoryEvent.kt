package pt.isel.repo

import pt.isel.User
import pt.isel.agenda.Event
import pt.isel.agenda.SelectionType

/**
 * Repository interface for managing events, extends the generic Repository
 */
interface RepositoryEvent : Repository<Event> {
    fun createEvent(
        title: String,
        description: String?,
        organizer: User,
        selectionType: SelectionType,
    ): Event
}
