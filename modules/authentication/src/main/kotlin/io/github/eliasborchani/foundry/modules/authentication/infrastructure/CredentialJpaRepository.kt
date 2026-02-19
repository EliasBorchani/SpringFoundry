package io.github.eliasborchani.foundry.modules.authentication.infrastructure

import io.github.eliasborchani.foundry.modules.authentication.domain.Credential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CredentialJpaRepository : JpaRepository<Credential, UUID> {
    fun findByEmail(email: String): Credential?
}
