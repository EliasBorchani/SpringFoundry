package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.domain.RefreshTokenEntity
import java.util.UUID

/**
 * Outbound port: persistence abstraction for [RefreshTokenEntity].
 * Implemented by the infrastructure layer.
 */
interface RefreshTokenRepository {
    fun save(refreshToken: RefreshTokenEntity): RefreshTokenEntity
    fun findByToken(token: String): RefreshTokenEntity?
    fun revokeAllByUserId(userId: UUID)
}
