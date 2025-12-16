package io.github.eliasborchani.foundry.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringFoundryApplication

fun main(args: Array<String>) {
    runApplication<SpringFoundryApplication>(*args)
}
