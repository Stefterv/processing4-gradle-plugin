plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`
}


repositories {
    mavenCentral()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}


dependencies {
    // Use JUnit test framework for unit tests
    implementation(project(":java"))
//    implementation("org.processing:java")
//    implementation("JsonKay:JsonKay")
    testImplementation("junit:junit:4.13")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.7.5")
    }
}
apply(plugin = "io.github.fvarrui.javapackager.plugin")

gradlePlugin {
    plugins {
        create("gradle-processing") {
            id = "org.processing.gradle"
            implementationClass = "org.processing.gradle.ProcessingPlugin"
        }
    }
}

