plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"

    kotlin("jvm") version "1.9.23"
}


repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}

group = "org.processing"
version = "1.0"

dependencies {

    // The preprocessing needs to be its own separate project, clear of the java library
    implementation(project(":java"))
    implementation(project(":app"))
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.7.5")
    }
}

gradlePlugin {
    plugins {
        create("gradleProcessing") {
            id = "org.processing.gradle"
            implementationClass = "org.processing.gradle.ProcessingPlugin"
        }
    }
}

publishing {
    repositories{
        mavenLocal()
    }
}