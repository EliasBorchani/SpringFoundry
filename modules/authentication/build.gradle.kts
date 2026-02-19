plugins {
    id("foundry.spring-module")
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // BCrypt password hashing — no full Spring Security stack required
    implementation("org.springframework.security:spring-security-crypto")
}
