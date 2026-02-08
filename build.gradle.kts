plugins {
    java
    id("org.springframework.boot") version "3.5.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.codenot"
version = "0.0.1-SNAPSHOT"
description = "conjunction_assessment_service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.hibernate.orm:hibernate-vector:6.4.4.Final")

    implementation("dev.langchain4j:langchain4j-core:1.11.0")
    // Anthropic (Claude) integration
    implementation("dev.langchain4j:langchain4j-anthropic:1.11.0")
    implementation("dev.langchain4j:langchain4j-anthropic-spring-boot-starter:1.6.0-beta12")

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
