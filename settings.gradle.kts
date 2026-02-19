pluginManagement {
    // build-logic provides the foundry.* convention plugins to all subprojects.
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    // Centralised repository declaration — subprojects must not add their own repositories{} blocks.
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "spring-foundry"

include(
    ":core:domain",
    ":core:auth",
    ":core:web",
    ":modules:users",
    ":modules:billing",
    ":modules:measurements",
    ":modules:authentication",
    ":apps:monolith-app",
    ":apps:users-app",
    ":apps:billing-app",
)
