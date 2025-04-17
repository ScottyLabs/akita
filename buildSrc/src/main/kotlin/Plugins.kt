import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// Common plugin application
fun Project.applyCommonPlugins() {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
}

// Spring Boot application configuration
fun Project.applySpringBootApplication() {
    applyCommonPlugins()
    apply(plugin = "org.springframework.boot")
}

// Library module configuration
fun Project.applyLibrary() {
    applyCommonPlugins()
    apply(plugin = "java-library")
}

// API module with OpenAPI generator
fun Project.applyApiModule() {
    applyLibrary()
    apply(plugin = "org.openapi.generator")
}

// JPA module configuration
fun Project.applyJpaModule() {
    applyCommonPlugins()
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
}