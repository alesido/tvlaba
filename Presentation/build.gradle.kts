plugins {
    id("presentation-module-conventions")
}

dependencies {
    implementation(project(mapOf("path" to ":Domain")))
}
