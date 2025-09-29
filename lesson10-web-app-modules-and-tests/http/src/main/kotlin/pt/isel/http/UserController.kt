package pt.isel.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.domain.AuthenticatedUser
import pt.isel.http.model.UserCreateTokenInputModel
import pt.isel.http.model.UserCreateTokenOutputModel
import pt.isel.http.model.UserHomeOutputModel
import pt.isel.http.model.UserInput
import pt.isel.service.UserAuthService

@RestController
class UserController(
    private val userService: UserAuthService,
) {
    /**
     * Try with:
     curl -i -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{
     "name": "Paul Atreides",
     "email": "paul@atreides.com",
     "password": "muadib"
     }'
     */
    @PostMapping("/api/users")
    fun createUser(
        @RequestBody userInput: UserInput,
    ): ResponseEntity<*> {
        val user =
            userService
                .createUser(userInput.name, userInput.email, userInput.password)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(
                "Location",
                "/api/users/${user.id}",
            ).build<Unit>()
    }

    /**
     * Try with:
     curl -i -X POST http://localhost:8080/api/users/token \
     -H "Content-Type: application/json" \
     -d '{
     "email": "paul@atreides.com",
     "password": "muadib"
     }'
     */
    @PostMapping("/api/users/token")
    fun token(
        @RequestBody input: UserCreateTokenInputModel,
    ): ResponseEntity<*> {
        val tokenInfo = userService.createToken(input.email, input.password)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(UserCreateTokenOutputModel(tokenInfo.tokenValue))
    }

    /**
     * This handler requires an authenticated user.
     * The {@link AuthenticatedUser} is resolved by an ArgumentResolver
     * using data extracted from the HTTP request headers.
     * Try:

     curl -i -X POST http://localhost:8080/api/logout
     -H "Authorization: Bearer lCZVAG-_OZx0Fq52MllDklc706vnLjGPWaMwRXKHJTM="

     */
    @PostMapping("api/logout")
    fun logout(user: AuthenticatedUser) {
        userService.revokeToken(user.token)
    }

    /**
     * This handler requires an authenticated user.
     * The {@link AuthenticatedUser} is resolved by an ArgumentResolver
     * using data extracted from the HTTP request headers.
     * Try:

     curl -i http://localhost:8080/api/me \
     -H "Authorization: Bearer lCZVAG-_OZx0Fq52MllDklc706vnLjGPWaMwRXKHJTM="

     */
    @GetMapping("/api/me")
    fun userHome(userAuthenticatedUser: AuthenticatedUser): ResponseEntity<UserHomeOutputModel> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(
                UserHomeOutputModel(
                    id = userAuthenticatedUser.user.id,
                    name = userAuthenticatedUser.user.name,
                    email = userAuthenticatedUser.user.email,
                ),
            )
}
