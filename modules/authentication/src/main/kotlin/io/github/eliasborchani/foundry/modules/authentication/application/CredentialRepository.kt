package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.domain.CredentialEntity

/**
 * Outbound port: persistence abstraction for [CredentialEntity].
 * Implemented by the infrastructure layer.
 */
interface CredentialRepository {
    fun save(credential: CredentialEntity): CredentialEntity
    fun findByEmail(email: String): CredentialEntity?
}
