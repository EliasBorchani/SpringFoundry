package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.application.dto.User
import java.util.UUID

/**
 * Inbound port: the public API of the users module, exposed to apps and controllers.
 * Implemented by [UserServiceImpl]. Apps depend only on this interface, never on the service directly.
 */
interface UserService {
    fun findById(id: UUID): User?
    fun findAll(): List<User>
    fun create(email: String, displayName: String): User
}
