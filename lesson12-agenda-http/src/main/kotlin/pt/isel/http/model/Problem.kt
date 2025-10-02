package pt.isel.http.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH =
    "https://github.com/isel-leic-daw/s2526i-51d-52d-public/tree/main/docs/problems"

sealed class Problem(
    typeUri: URI,
) {
    @Suppress("unused")
    val type = typeUri.toString()
    val title = typeUri.toString().split("/").last()

    fun response(status: HttpStatus): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(this)

    data object EmailAlreadyInUse : Problem(URI("$PROBLEM_URI_PATH/email-already-in-use"))

    data object ParticipantNotFound : Problem(URI("$PROBLEM_URI_PATH/participant-not-found"))

    data object EventNotFound : Problem(URI("$PROBLEM_URI_PATH/event-not-found"))

    data object TimeSlotNotFound : Problem(URI("$PROBLEM_URI_PATH/event-not-found"))

    data object TimeSlotAlreadyAllocated : Problem(URI("$PROBLEM_URI_PATH/timeslot-already-allocated"))

    data object UserIsAlreadyParticipantInTimeSlot :
        Problem(URI("$PROBLEM_URI_PATH/user-is-already-participant-in-time-slot"))

    data object InsecurePassword : Problem(URI("$PROBLEM_URI_PATH/insecure-password"))

    data object UserOrPasswordAreInvalid : Problem(URI("$PROBLEM_URI_PATH/user-or-password-are-invalid"))

    data object InvalidRequestContent : Problem(URI("$PROBLEM_URI_PATH/invalid-request-content"))
}
