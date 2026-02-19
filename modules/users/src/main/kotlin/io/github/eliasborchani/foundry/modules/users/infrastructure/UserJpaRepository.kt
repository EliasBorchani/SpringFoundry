package io.github.eliasborchani.foundry.modules.users.infrastructure

import io.github.eliasborchani.foundry.modules.users.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean
}
