package io.github.eliasborchani.foundry.modules.authentication.infrastructure

import io.github.eliasborchani.foundry.modules.authentication.application.CredentialRepository
import io.github.eliasborchani.foundry.modules.authentication.domain.Credential
import org.springframework.stereotype.Repository

@Repository
class CredentialRepositoryAdapter(
    private val jpa: CredentialJpaRepository,
) : CredentialRepository {

    override fun save(credential: Credential): Credential = jpa.save(credential)

    override fun findByEmail(email: String): Credential? = jpa.findByEmail(email)
}
