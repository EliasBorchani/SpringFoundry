// Convention plugin for feature modules (users, billing, measurements) and JPA core modules.
// Adds Spring Data JPA; kotlin-spring (open classes) and kotlin-jpa (no-arg constructors).
plugins {
    id("foundry.kotlin-library")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
