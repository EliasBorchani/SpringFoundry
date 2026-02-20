// Convention plugin for deployable Spring Boot apps (monolith-app, users-app, billing-app).
// The Spring Boot plugin re-applies the BOM, so no explicit platform() needed here.
plugins {
    id("foundry.kotlin-library")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
