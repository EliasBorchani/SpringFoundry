package io.github.eliasborchani.foundry.apps.users.controller

import io.github.eliasborchani.foundry.modules.users.application.UserPort
import io.github.eliasborchani.foundry.modules.users.application.dto.UserDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(private val userPort: UserPort) {

    @GetMapping
    fun getAll(): List<UserDto> = userPort.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<UserDto> =
        userPort.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateUserRequest): UserDto =
        userPort.create(request.email, request.displayName)
}

data class CreateUserRequest(val email: String, val displayName: String)
