plugins {
    id("java-gradle-plugin")
//    id("com.gradle.plugin-publish") version "1.2.1"

    kotlin("jvm") version "1.9.23"
}


repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}

group = "org.processing"
version = "4.3.1"

dependencies {
    implementation("org.processing:preprocessor:${version}")
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