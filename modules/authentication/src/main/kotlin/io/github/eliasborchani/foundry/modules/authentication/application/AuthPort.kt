package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.application.dto.TokenPairDto
import java.util.UUID

/**
 * Inbound port: the public API of the authentication module.
 * Implemented by [AuthService]. Apps depend only on this interface.
 */
interface AuthPort {
    fun register(userId: UUID, email: String, rawPassword: String)
    fun login(email: String, rawPassword: String): TokenPairDto
    fun refresh(refreshToken: String): TokenPairDto
    fun revoke(refreshToken: String)
}
