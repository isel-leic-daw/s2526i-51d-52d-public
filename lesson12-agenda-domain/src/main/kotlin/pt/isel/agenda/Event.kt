package pt.isel.agenda

import pt.isel.User

/**
 * Represents a scheduling event
 *
 * Class diagram in yuml.me as:
[User|id:Int; name:String; email:String]
[Participant|id:Int; user:User; slot:TimeSlotMultiple]
[Event|id:Int; title:String; description:String; organizer:User; selectionType:SelectionType]
[SelectionType|SINGLE; MULTIPLE]
[TimeSlot|id:Int; startTime:LocalDateTime; durationInMinutes:Int; event:Event]
[TimeSlotSingle|owner:User?]
[TimeSlotMultiple]

// Define relationships
[Event]<1-[TimeSlot]
[Event]->[SelectionType]
[Participant]-1>[TimeSlotMultiple]
[Participant]-1>[pt.isel.User]
[TimeSlot]^-[TimeSlotSingle]
[TimeSlot]^-[TimeSlotMultiple]
[TimeSlotSingle]->1[pt.isel.User]
 */
data class Event(
    val id: Int,
    val title: String,
    val description: String?,
    val organizer: User,
    // Indicates whether the event allows single or multiple selections
    val selectionType: SelectionType,
)
