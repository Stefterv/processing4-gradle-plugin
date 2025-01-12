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
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
}

gradlePlugin {
    // TODO: Publishing https://docs.gradle.org/current/userguide/publishing_gradle_plugins.html
    // TODO: Publishing CI/CD
    plugins {
        create("gradleProcessing") {
            id = "org.processing"
            implementationClass = "org.processing.gradle.ProcessingPlugin"
        }
    }
}
publishing {
    repositories {
        mavenLocal()
    }
}