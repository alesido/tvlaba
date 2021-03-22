import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
    id("kotlin-kapt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    // Kotlin
    implementation(Libs.kotlin)
    implementation(Libs.kotlinReflect)

    // Dependency Injection
    implementation(Libs.javaxAnnotation)
    implementation(Libs.javaxInject)

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
    compile(Libs.jodaTime)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)
}
