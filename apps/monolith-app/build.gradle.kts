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

    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}
