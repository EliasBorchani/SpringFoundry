package io.github.eliasborchani.foundry.modules.authentication.infrastructure

import io.github.eliasborchani.foundry.modules.authentication.application.RefreshTokenRepository
import io.github.eliasborchani.foundry.modules.authentication.domain.RefreshToken
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RefreshTokenRepositoryAdapter(
    private val jpa: RefreshTokenJpaRepository,
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken): RefreshToken = jpa.save(refreshToken)

    override fun findByToken(token: String): RefreshToken? = jpa.findByToken(token)

    override fun revokeAllByUserId(userId: UUID) = jpa.revokeAllByUserId(userId)
}
