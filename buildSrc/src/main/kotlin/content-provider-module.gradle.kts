plugins {

    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    //id("io.objectbox") // cannot include this into a precompiled Kotlin DSL script
}

android {
    compileSdkVersion(Versions.androidCompileSdk)

    defaultConfig {
        minSdkVersion(Versions.androidMinSdk)
        targetSdkVersion(Versions.androidTargetSdk)
        multiDexEnabled = true
    }

    dexOptions {
        preDexLibraries = false
        dexInProcess = false
        javaMaxHeapSize = "4g"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_1_8}"
    }

    packagingOptions {
        exclude("LICENSE.txt")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
    }

    lintOptions {
        isQuiet = true
        isAbortOnError = false
        isIgnoreWarnings = true
        disable("InvalidPackage")            //Some libraries have issues with this.
        disable("OldTargetApi")              //Lint gives this warning but SDK 20 would be Android L Beta.
        disable("IconDensities")             //For testing purpose. This is safe to remove.
        disable("IconMissingDensityFolder")  //For testing purpose. This is safe to remove.
    }
}

kapt {
    correctErrorTypes = true
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
