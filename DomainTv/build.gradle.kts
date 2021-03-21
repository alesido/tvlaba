plugins {
    id("kotlin")
}

dependencies {
    compile(project(":Domain"))

    // Kotlin
    implementation(Libs.kotlin)

    // Dependency Injection
    implementation(Libs.javaxAnnotation)
    implementation(Libs.javaxInject)

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxJava)

    // Miscellaneous
    compile(Libs.jodaTime)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
}
