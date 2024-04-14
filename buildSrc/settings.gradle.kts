rootProject.name = "gradle"

include("core")
project(":core").projectDir = file("../../processing4-gradle/core")

include("app")
project(":app").projectDir = file("../../processing4-gradle/app")

include("java")
project(":java").projectDir = file("../../processing4-gradle/java")
