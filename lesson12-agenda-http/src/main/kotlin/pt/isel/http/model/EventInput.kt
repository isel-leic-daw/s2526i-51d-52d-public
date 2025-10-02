package pt.isel.http.model

import pt.isel.agenda.SelectionType

data class EventInput(
    val title: String,
    val description: String?,
    val selectionType: SelectionType,
)
