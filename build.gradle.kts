plugins {
    id("org.processing.gradle")
    id("application")
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.processing"
version = "1.0-SNAPSHOT"

application{
    mainClass = "RGBCube"
}

dependencies{
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.5.0")
    implementation("org.jogamp.jogl:jogl-all-main:2.5.0")
}

tasks.compileKotlin { dependsOn(tasks.generateGrammarSource) }

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jogamp.org/deployment/maven")
}

kotlin {
    jvmToolchain(21)
}