plugins {
    id("org.processing.gradle")
    id("application")
    kotlin("jvm") version "1.9.23"
}

group = "org.processing"
version = "1.0-SNAPSHOT"

application{
    mainClass = "RGBCube"
}

dependencies{
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.compileKotlin { dependsOn(tasks.generateGrammarSource) }

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}