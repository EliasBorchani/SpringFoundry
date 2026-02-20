package io.github.eliasborchani.foundry.modules.authentication.infrastructure

import io.github.eliasborchani.foundry.modules.authentication.application.CredentialRepository
import io.github.eliasborchani.foundry.modules.authentication.domain.CredentialEntity
import org.springframework.stereotype.Repository

@Repository
class CredentialRepositoryAdapter(
    private val jpa: CredentialJpaRepository,
) : CredentialRepository {

    override fun save(credential: CredentialEntity): CredentialEntity = jpa.save(credential)

    override fun findByEmail(email: String): CredentialEntity? = jpa.findByEmail(email)
}
