package io.github.eliasborchani.foundry.modules.users.infrastructure

import io.github.eliasborchani.foundry.modules.users.application.UserRepository
import io.github.eliasborchani.foundry.modules.users.domain.UserEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository,
) : UserRepository {

    override fun findById(id: UUID): UserEntity? = jpa.findById(id).orElse(null)

    override fun findAll(): List<UserEntity> = jpa.findAll()

    override fun save(user: UserEntity): UserEntity = jpa.save(user)

    override fun existsByEmail(email: String): Boolean = jpa.existsByEmail(email)
}
