package io.github.eliasborchani.foundry.modules.users.application.dto

import io.github.eliasborchani.foundry.modules.users.domain.User
import java.time.Instant
import java.util.UUID

data class UserDto(
    val id: UUID,
    val email: String,
    val displayName: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User) = UserDto(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            createdAt = user.createdAt,
        )
    }
}
