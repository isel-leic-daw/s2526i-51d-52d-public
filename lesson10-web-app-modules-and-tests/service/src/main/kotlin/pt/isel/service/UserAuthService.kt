package pt.isel.service

import jakarta.inject.Named
import org.springframework.security.crypto.password.PasswordEncoder
import pt.isel.domain.PasswordValidationInfo
import pt.isel.domain.Token
import pt.isel.domain.TokenEncoder
import pt.isel.domain.TokenExternalInfo
import pt.isel.domain.User
import pt.isel.domain.UsersDomainConfig
import pt.isel.repo.RepositoryUser
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Base64.getUrlDecoder
import java.util.Base64.getUrlEncoder

sealed class UserError {
    data object AlreadyUsedEmailAddress : UserError()

    data object InsecurePassword : UserError()
}

sealed class TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError()
}

@Named
class UserAuthService(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig,
    private val repoUsers: RepositoryUser,
    private val clock: Clock,
) {
    private fun validatePassword(
        password: String,
        validationInfo: PasswordValidationInfo,
    ) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo,
    )

    private fun createPasswordValidationInformation(password: String) =
        PasswordValidationInfo(
            validationInfo = passwordEncoder.encode(password),
        )

    // TODO it could be better
    fun isSafePassword(password: String) = password.length > 4

    /**
     * Still missing validation if given email already exists.
     */
    fun createUser(
        name: String,
        email: String,
        password: String,
    ): Either<UserError, User> {
        if (!isSafePassword(password)) {
            return failure(UserError.InsecurePassword)
        }
        if (repoUsers.findByEmail(email) != null) {
            return failure(UserError.AlreadyUsedEmailAddress)
        }
        val passwordValidationInfo = createPasswordValidationInformation(password)
        return success(repoUsers.createUser(name, email, passwordValidationInfo))
    }

    fun createToken(
        email: String,
        password: String,
    ): TokenExternalInfo { // TO DO: Replace by Either
        require(email.isNotBlank()) { "Email cannot be blank" } // Replace by Either.Failure
        require(password.isNotBlank()) { "Password cannot be blank" } // Replace by Either.Failure

        val user = repoUsers.findByEmail(email)
        requireNotNull(user) // Replace by Either.Failure

        if (!validatePassword(password, user.passwordValidation)) {
            throw IllegalArgumentException("Passwords do not match") // Replace by Either.Failure
        }
        val tokenValue = generateTokenValue()
        val now = clock.instant()
        val newToken =
            Token(
                tokenEncoder.createValidationInformation(tokenValue),
                user.id,
                createdAt = now,
                lastUsedAt = now,
            )
        repoUsers.createToken(newToken, config.maxTokensPerUser)
        return TokenExternalInfo(
            tokenValue,
            getTokenExpiration(newToken),
        )
    }

    fun revokeToken(token: String): Boolean {
        val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
        repoUsers.removeTokenByValidationInfo(tokenValidationInfo)
        return true
    }

    fun getUserByToken(token: String): User? {
        if (!canBeToken(token)) {
            return null
        }
        val tokenValidationInfo = tokenEncoder.createValidationInformation(token)

        val userAndToken: Pair<User, Token>? = repoUsers.getTokenByTokenValidationInfo(tokenValidationInfo)
        return if (userAndToken != null && isTokenTimeValid(clock, userAndToken.second)) {
            repoUsers.updateTokenLastUsed(userAndToken.second, clock.instant())
            userAndToken.first
        } else {
            null
        }
    }

    private fun canBeToken(token: String): Boolean =
        try {
            getUrlDecoder().decode(token).size == config.tokenSizeInBytes
        } catch (ex: IllegalArgumentException) {
            false
        }

    private fun isTokenTimeValid(
        clock: Clock,
        token: Token,
    ): Boolean {
        val now = clock.instant()
        return token.createdAt <= now &&
            Duration.between(now, token.createdAt) <= config.tokenTtl &&
            Duration.between(now, token.lastUsedAt) <= config.tokenRollingTtl
    }

    private fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            getUrlEncoder().encodeToString(byteArray)
        }

    private fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }
}
