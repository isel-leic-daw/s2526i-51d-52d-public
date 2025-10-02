package pt.isel.service

/**
---
config:
theme: neo
look: classic
---
classDiagram
class Either~L, R~ { <<sealed>> }
class Left~L~ { value: L }
class Right~R~ { value: R }
Either <|-- Left
Either <|-- Right
class Success~S~ {
<<alias for Right<S>>>
}
class Failure~F~ {
<<alias for Left<F>>>
}
 */
sealed class Either<out L, out R> {
    data class Left<out L>(
        val value: L,
    ) : Either<L, Nothing>()

    data class Right<out R>(
        val value: R,
    ) : Either<Nothing, R>()
}

// Functions for when using Either to represent success or failure
fun <R> success(value: R) = Either.Right(value)

fun <L> failure(error: L) = Either.Left(error)

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>
