package pt.isel.domain

import java.time.Instant

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant,
)
