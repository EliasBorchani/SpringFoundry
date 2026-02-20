package io.github.eliasborchani.foundry.modules.users.application.dto

import io.github.eliasborchani.foundry.modules.users.domain.UserEntity
import java.time.Instant
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val displayName: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: UserEntity) = User(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            createdAt = user.createdAt,
        )
    }
}
