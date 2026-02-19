package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.application.dto.UserDto
import java.util.UUID

/**
 * Inbound port: the public API of the users module, exposed to apps and controllers.
 * Implemented by [UserService]. Apps depend only on this interface, never on the service directly.
 */
interface UserPort {
    fun findById(id: UUID): UserDto?
    fun findAll(): List<UserDto>
    fun create(email: String, displayName: String): UserDto
}
