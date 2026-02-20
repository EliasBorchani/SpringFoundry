package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.domain.UserEntity
import java.util.UUID

/**
 * Outbound port: data-access contract used by [UserServiceImpl].
 * Implemented in the infrastructure layer by [UserRepositoryAdapter].
 * Keeps the application layer free of JPA/Spring Data types.
 */
interface UserRepository {
    fun findById(id: UUID): UserEntity?
    fun findAll(): List<UserEntity>
    fun save(user: UserEntity): UserEntity
    fun existsByEmail(email: String): Boolean
}
