package pt.isel.repo

import pt.isel.domain.PasswordValidationInfo
import pt.isel.domain.Token
import pt.isel.domain.TokenValidationInfo
import pt.isel.domain.User
import java.time.Instant

/**
 * Repository interface for managing users, extends the generic Repository
 */
interface RepositoryUser : Repository<User> {
    fun createUser(
        name: String,
        email: String,
        passwordValidation: PasswordValidationInfo,
    ): User

    fun findByEmail(email: String): User?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun createToken(
        token: Token,
        maxTokens: Int,
    )

    fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    )

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}
