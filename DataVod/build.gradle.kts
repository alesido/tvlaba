plugins {
    id("domain-module-conventions")
}

dependencies {
    implementation(project(":Domain"))
    implementation(project(":DomainVod"))
    implementation(project(":Data"))
}
