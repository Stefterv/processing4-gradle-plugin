plugins {
    id("org.processing.gradle")
    id("application")
}

group = "org.processing"
version = "1.0-SNAPSHOT"

application{
    mainClass = "Brightness"
}

dependencies{
    implementation("com.google.code.gson:gson:2.10.1")
}


repositories {
    mavenCentral()
}