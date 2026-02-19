package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.domain.RefreshToken
import java.util.UUID

/**
 * Outbound port: persistence abstraction for [RefreshToken].
 * Implemented by the infrastructure layer.
 */
interface RefreshTokenRepository {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByToken(token: String): RefreshToken?
    fun revokeAllByUserId(userId: UUID)
}
