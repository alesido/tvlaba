plugins {
    id("domain-module-conventions")
}

dependencies {
    implementation(project(":Domain"))
    implementation(project(":DomainTv"))
    implementation(project(":Data"))
}
