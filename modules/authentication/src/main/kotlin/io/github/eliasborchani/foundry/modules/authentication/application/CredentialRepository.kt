package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.domain.Credential

/**
 * Outbound port: persistence abstraction for [Credential].
 * Implemented by the infrastructure layer.
 */
interface CredentialRepository {
    fun save(credential: Credential): Credential
    fun findByEmail(email: String): Credential?
}
