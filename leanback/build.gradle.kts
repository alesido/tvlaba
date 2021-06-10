
plugins {

    id("com.android.library")
}

android {

    compileSdkVersion(Versions.androidCompileSdk)

    defaultConfig {
        minSdkVersion(Versions.androidMinSdk)
    }

    sourceSets {
        named("main") {
            java.srcDir("common")
            java.srcDir("kitkat")
            java.srcDir("api21")
        }
    }
}

dependencies {
        api(AndroidX.annotation)
        api(AndroidX.interpolator)
        api(AndroidX.core)
        implementation(AndroidX.collection)
        api(AndroidX.media)
        api(AndroidX.fragment)
        api (AndroidX.recyclerView)
        api(AndroidX.appCompat)

        api(Libs.exoplayer)
}

