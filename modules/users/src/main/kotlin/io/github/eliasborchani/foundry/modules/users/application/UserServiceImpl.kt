package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.application.dto.User
import io.github.eliasborchani.foundry.modules.users.domain.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun findById(id: UUID): User? =
        userRepository.findById(id)?.let(User::from)

    override fun findAll(): List<User> =
        userRepository.findAll().map(User::from)

    @Transactional
    override fun create(email: String, displayName: String): User {
        require(!userRepository.existsByEmail(email)) { "Email $email is already registered" }
        val user = userRepository.save(UserEntity(email = email, displayName = displayName))
        return User.from(user)
    }
}
