package io.github.eliasborchani.foundry.apps.monolith

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices

@AnalyzeClasses(
    packages = ["io.github.eliasborchani.foundry"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest {

    /**
     * No module may import classes from another module.
     * Cross-module calls are only allowed in the apps layer.
     */
    @ArchTest
    val modulesShouldBeDecoupled: ArchRule =
        slices()
            .matching("io.github.eliasborchani.foundry.modules.(*)..")
            .should().notDependOnEachOther()
            .`as`("Modules must not depend on each other")

    /**
     * @RestController is only allowed inside the apps layer.
     */
    @ArchTest
    val controllersOnlyInApps: ArchRule =
        noClasses()
            .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().resideOutsideOfPackage("io.github.eliasborchani.foundry.apps..")
            .`as`("@RestController must only reside in apps packages")

    /**
     * Domain layer must not depend on Spring (keeps it framework-agnostic).
     */
    @ArchTest
    val domainMustNotDependOnSpring: ArchRule =
        noClasses()
            .that().resideInAPackage("..modules.*.domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .`as`("Domain classes must not depend on Spring")
}
