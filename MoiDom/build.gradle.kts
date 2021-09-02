plugins {
    id("content-provider-module")
    id("io.objectbox")
}

dependencies {

    implementation(Libs.jodaTime)
    implementation(project(mapOf("path" to ":DataVod")))
}
