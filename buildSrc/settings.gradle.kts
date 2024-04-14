// Figure this out later
sourceControl {
    gitRepository( uri("https://github.com/Stefterv/processing4-gradle.git")) {
//        producesModule("org.processing:core")
//        producesModule("org.processing:app")
        producesModule("org.processing:java")
//        rootDir = "java/"
    }
}


include("core")
project(":core").projectDir = file("../../processing4-gradle/core")

include("app")
project(":app").projectDir = file("../../processing4-gradle/app")

include("java")
project(":java").projectDir = file("../../processing4-gradle/java")
