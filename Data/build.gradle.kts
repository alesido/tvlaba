
plugins {
    id("domain-module-conventions")
}

dependencies {
    implementation(project(":Domain"))
    implementation(Libs.jodaTime)
}
