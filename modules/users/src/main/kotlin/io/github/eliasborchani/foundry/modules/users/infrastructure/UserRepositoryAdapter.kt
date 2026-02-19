package io.github.eliasborchani.foundry.modules.users.infrastructure

import io.github.eliasborchani.foundry.modules.users.application.UserRepository
import io.github.eliasborchani.foundry.modules.users.domain.User
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository,
) : UserRepository {

    override fun findById(id: UUID): User? = jpa.findById(id).orElse(null)

    override fun findAll(): List<User> = jpa.findAll()

    override fun save(user: User): User = jpa.save(user)

    override fun existsByEmail(email: String): Boolean = jpa.existsByEmail(email)
}
