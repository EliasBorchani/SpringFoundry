package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.application.dto.TokenPair
import java.util.UUID

/**
 * Inbound port: the public API of the authentication module.
 * Implemented by [AuthServiceImpl]. Apps depend only on this interface.
 */
interface AuthService {
    fun register(userId: UUID, email: String, rawPassword: String)
    fun login(email: String, rawPassword: String): TokenPair
    fun refresh(refreshToken: String): TokenPair
    fun revoke(refreshToken: String)
}
