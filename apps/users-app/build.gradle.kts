plugins {
    id("foundry.spring-app")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:auth"))
    implementation(project(":core:web"))
    implementation(project(":modules:users"))
}
