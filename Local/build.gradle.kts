plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("io.objectbox")
}

android {

    compileSdkVersion(Versions.androidCompileSdk)
    defaultConfig {
        minSdkVersion(Versions.androidMinSdk)
        targetSdkVersion(Versions.androidTargetSdk)
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
        jvmTarget = "1.8"
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

//    testOptions {
//        unitTests.all {
//            jvmArgs = "-noverify"
//        }
//    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    api(project(":Domain"))
    api(project(":Data"))
    api(project(":DataTv"))

    implementation(Libs.kotlin)
    implementation(Libs.rxKotlin)
    implementation(Libs.rxAndroid)
    implementation(Libs.kotlinReflect)

    //implementation localDependencies.appCompatV7

    dependencyInjection()

    implementation(Libs.objectboxAndroid)
    implementation(Libs.objectboxKotlin)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.kotlinJUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)

    testImplementation(Libs.objectboxTestLinux)
    testImplementation(Libs.objectboxTestMacOS)
    testImplementation(Libs.objectboxTestWindows)
}
