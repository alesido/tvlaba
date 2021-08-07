import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("android-library-module")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    // Kotlin
    implementation(Libs.kotlin)
    implementation(Libs.kotlinReflect)

    // Dependency Injection
    dependencyInjection()

    // RX
    implementation(Libs.rxKotlin)
    implementation(Libs.rxJava)

    // Gson + okHttp
    implementation(Libs.gson)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpLogger)

    // Retrofit
    implementation(Libs.retrofit)
    implementation(Libs.retrofitGson)
    implementation(Libs.retrofitAdapter)


    // Miscellaneous
    implementation(Libs.jodaTime)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
}
