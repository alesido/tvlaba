plugins {

    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    //id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(Versions.androidCompileSdk)
    defaultConfig {
        minSdkVersion(Versions.androidMinSdk)
        targetSdkVersion(Versions.androidTargetSdk)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_1_8}"
    }
    androidExtensions {
        isExperimental = true
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "android.support") {
            if (!requested.name.startsWith("multidex")) {
                useVersion("26.+")
            }
        }
    }
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

    // Layout & Design
    implementation(Libs.recyclerView)
    implementation(Libs.materialDesign)
    implementation(Libs.constraintLayout)

    implementation(Libs.viewBinding)

    // Architectural Components
    archComponents()

    // Miscellaneous
    implementation(Libs.timber)
    implementation(Libs.glide)
}
