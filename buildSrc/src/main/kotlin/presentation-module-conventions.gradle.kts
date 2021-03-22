plugins {
    id("android-library-module")
}

dependencies {

    // Kotlin
    implementation(Libs.kotlin)

    // Dependency Injection
    annotations()
    dependencyInjection()

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxAndroid)
    implementation(Libs.rxJava)

    archComponents()

    // Logging
    implementation(Libs.timber)

    // Testing
    testImplementation(Libs.jUnit)
    testImplementation(Libs.kotlinJUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
    testImplementation(Libs.robolectric)
}
