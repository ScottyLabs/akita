plugins {
	id("org.springframework.boot") version Versions.springBoot
	id("io.spring.dependency-management") version Versions.springDependencyManagement
	kotlin("jvm") version Versions.kotlin
	kotlin("plugin.spring") version Versions.kotlin
}

group = "org.scottylabs.akita"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(Versions.javaVersion)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(Deps.springBootWebflux)
	implementation(Deps.springCloudGateway)
	implementation(Deps.springBootSecurity)
	implementation(Deps.springBootOauth2Client)
	implementation(Deps.springBootActuator)

	implementation(Deps.springBootDataRedis)
	implementation(Deps.springSessionRedis)

	runtimeOnly(Deps.jwtImpl)
	runtimeOnly(Deps.jwtJackson)

	testImplementation(Deps.springBootTest)
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
		mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.springBoot}")
	}
}
