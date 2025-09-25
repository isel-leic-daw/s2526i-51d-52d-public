package pt.isel.domain

import java.time.Instant


class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant,
)
