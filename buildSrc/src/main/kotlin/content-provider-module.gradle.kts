plugins {
    id("android-library-module")
    //id("io.objectbox") // cannot include this into a precompiled Kotlin DSL script
}

dependencies {

    api(project(":Domain"))
    api(project(":Data"))
    api(project(":DataTv"))
    api(project(":Remote"))
    api(project(":Local"))

    // Kotlin
    implementation(Libs.kotlin)
    implementation(Libs.kotlinReflect)

    // Dependency Injection
    annotations()
    dependencyInjection()

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxAndroid)
    implementation(Libs.rxJava)

    // Gson + okHttp
    implementation(Libs.gson)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpLogger)

    // Retrofit
    implementation(Libs.retrofit)
    implementation(Libs.retrofitGson)
    implementation(Libs.retrofitAdapter)

    // ObjectBox
    implementation(Libs.objectboxAndroid)
    implementation(Libs.objectboxKotlin)

    // Logging
    implementation(Libs.timber)

    // Testing
    testImplementation(Libs.jUnit)
    testImplementation(Libs.kotlinJUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
    testImplementation(Libs.okHttpMockWebServer)
    testImplementation(Libs.timberUnitRule)
}
