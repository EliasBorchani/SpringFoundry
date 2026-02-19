package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.domain.User
import java.util.UUID

/**
 * Outbound port: data-access contract used by [UserService].
 * Implemented in the infrastructure layer by [UserRepositoryAdapter].
 * Keeps the application layer free of JPA/Spring Data types.
 */
interface UserRepository {
    fun findById(id: UUID): User?
    fun findAll(): List<User>
    fun save(user: User): User
    fun existsByEmail(email: String): Boolean
}
