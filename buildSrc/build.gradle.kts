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
version = "4.3.1"

dependencies {
    implementation("org.processing:preprocessor:${version}")
}

gradlePlugin {
    plugins {
        create("gradleProcessing") {
            id = "org.processing"
            implementationClass = "org.processing.gradle.ProcessingPlugin"
        }
    }
}