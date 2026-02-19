package io.github.eliasborchani.foundry.apps.monolith

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

// scanBasePackages covers all modules so Spring finds @Service, @Repository beans.
// @EntityScan and @EnableJpaRepositories mirror that scan explicitly for JPA.
@SpringBootApplication(scanBasePackages = ["io.github.eliasborchani.foundry"])
@EntityScan("io.github.eliasborchani.foundry")
@EnableJpaRepositories("io.github.eliasborchani.foundry")
class MonolithApplication

fun main(args: Array<String>) {
    runApplication<MonolithApplication>(*args)
}
