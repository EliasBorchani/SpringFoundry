package io.github.eliasborchani.foundry.apps.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["io.github.eliasborchani.foundry"])
@EntityScan("io.github.eliasborchani.foundry")
@EnableJpaRepositories("io.github.eliasborchani.foundry")
class UsersApplication

fun main(args: Array<String>) {
    runApplication<UsersApplication>(*args)
}
