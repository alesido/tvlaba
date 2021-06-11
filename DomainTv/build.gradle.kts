plugins {
    id("kotlin")
}

dependencies {

    implementation(project(":Domain"))

    // Kotlin
    implementation(Libs.kotlin)

    // Dependency Injection
    implementation(Libs.javaxAnnotation)
    implementation(Libs.javaxInject)

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxJava)

    // Miscellaneous
    implementation(Libs.jodaTime)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
}
