package pt.isel.domain

class AuthenticatedUser(
    val user: User,
    val token: String,
)
