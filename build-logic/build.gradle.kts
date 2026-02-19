plugins {
    `kotlin-dsl`
}

dependencies {
    // Expose the version catalog type-safe accessors (LibrariesForLibs) to convention plugins.
    // https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // Versions must stay in sync with gradle/libs.versions.toml.
    // (The catalog's type-safe accessors are not yet available at this compilation stage.)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.2.21")     // provides kotlin("plugin.spring")
    implementation("org.jetbrains.kotlin:kotlin-noarg:2.2.21")       // provides kotlin("plugin.jpa")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.0.0")
}
