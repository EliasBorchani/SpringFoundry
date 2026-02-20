package io.github.eliasborchani.foundry.apps.monolith.controller

import io.github.eliasborchani.foundry.modules.authentication.application.AuthService
import io.github.eliasborchani.foundry.modules.authentication.application.dto.TokenPair
import io.github.eliasborchani.foundry.modules.users.application.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequest): TokenPair {
        val user = userService.create(email = request.email, displayName = request.displayName)
        authService.register(userId = user.id, email = request.email, rawPassword = request.password)
        return authService.login(email = request.email, rawPassword = request.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenPair =
        try {
            authService.login(email = request.email, rawPassword = request.password)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): TokenPair =
        try {
            authService.refresh(refreshToken = request.refreshToken)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@RequestBody request: RefreshRequest) {
        authService.revoke(refreshToken = request.refreshToken)
    }
}

data class RegisterRequest(val email: String, val displayName: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class RefreshRequest(val refreshToken: String)
