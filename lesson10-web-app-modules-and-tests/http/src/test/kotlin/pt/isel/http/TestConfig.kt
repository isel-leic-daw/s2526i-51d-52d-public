package pt.isel.http

import io.mockk.every
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pt.isel.domain.PasswordValidationInfo
import pt.isel.domain.Sha256TokenEncoder
import pt.isel.domain.TokenExternalInfo
import pt.isel.domain.User
import pt.isel.domain.UsersDomainConfig
import pt.isel.repo.RepositoryUser
import pt.isel.service.UserAuthService
import pt.isel.service.success
import java.time.Clock
import kotlin.math.abs
import kotlin.random.Random

private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

@Configuration
@ComponentScan("pt.isel")
class TestConfig {
    companion object {
        val roseMary =
            User(
                id = 11,
                name = "Rose Mary",
                email = "rose@example.com",
                passwordValidation = PasswordValidationInfo("rainhaDoCaisSodre"),
            )
        val users =
            mutableMapOf<Int, User>(
                roseMary.id to roseMary,
            )
    }

    @Bean
    @Primary
    fun mockUserAuthService(): UserAuthService {
        var userId = 17
        val mock = mockk<UserAuthService>(relaxed = true)

        // Emulate createUser behaviour
        every { mock.createUser(any(), any(), any()) } answers {
            success(
                User(
                    id = ++userId,
                    name = firstArg(),
                    email = secondArg(),
                    passwordValidation = PasswordValidationInfo(thirdArg()),
                ).also { users[it.id] = it },
            )
        }

        // Emulate createToken behaviour
        every { mock.createToken(any(), any()) } returns
            TokenExternalInfo(
                newTokenValidationData(),
                Clock.systemUTC().instant(),
            )

        // Emulate revokeToken behaviour
        every { mock.revokeToken(any()) } returns true

        // Add more emulated behaviours as needed

        return mock
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun usersDomainConfig() = mockk<UsersDomainConfig>(relaxed = true)

    @Bean
    fun repoUser() = mockk<RepositoryUser>(relaxed = true)
}
