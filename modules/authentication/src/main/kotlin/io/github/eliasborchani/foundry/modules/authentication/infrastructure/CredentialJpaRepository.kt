package io.github.eliasborchani.foundry.modules.authentication.infrastructure

import io.github.eliasborchani.foundry.modules.authentication.domain.CredentialEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CredentialJpaRepository : JpaRepository<CredentialEntity, UUID> {
    fun findByEmail(email: String): CredentialEntity?
}
