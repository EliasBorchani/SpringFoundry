package io.github.eliasborchani.foundry.modules.users.application

import io.github.eliasborchani.foundry.modules.users.application.dto.UserDto
import io.github.eliasborchani.foundry.modules.users.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) : UserPort {

    override fun findById(id: UUID): UserDto? =
        userRepository.findById(id)?.let(UserDto::from)

    override fun findAll(): List<UserDto> =
        userRepository.findAll().map(UserDto::from)

    @Transactional
    override fun create(email: String, displayName: String): UserDto {
        require(!userRepository.existsByEmail(email)) { "Email $email is already registered" }
        val user = userRepository.save(User(email = email, displayName = displayName))
        return UserDto.from(user)
    }
}
