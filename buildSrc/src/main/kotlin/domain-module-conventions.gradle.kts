

plugins {
    id("kotlin")
    id("kotlin-kapt")
}

dependencies {

    // Kotlin
    implementation(Libs.kotlin)

    // Dependency Injection
    implementation(Libs.javaxAnnotation)
    dependencyInjection()

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxJava)

    // Miscellaneous
    implementation(Libs.jodaTime)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
}
