package pt.isel.http.model

data class UserCreateTokenInputModel(
    val email: String,
    val password: String,
)
