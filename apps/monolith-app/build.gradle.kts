plugins {
    id("foundry.spring-app")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:auth"))
    implementation(project(":core:web"))
    implementation(project(":modules:users"))
    implementation(project(":modules:measurements"))
    implementation(project(":modules:authentication"))

    implementation(libs.starter.flyway)
    runtimeOnly(libs.flyway.postgresql)

    runtimeOnly(libs.postgresql)

    testRuntimeOnly(libs.h2)
    testImplementation(libs.archunit.junit5)
}
