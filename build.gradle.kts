plugins {
    id("org.processing.gradle")
    id("application")
}

group = "org.processing"
version = "1.0-SNAPSHOT"

application{
    mainClass = "Brightness"
}


repositories {
    mavenCentral()
}