plugins {
    // custom precompiled plugin for conventional part of the build configuration
    id("ui-app-module-conventions")
    // JetPack's navigation plugin, cannot provide it via the precompiled plugin (?)
    id("androidx.navigation.safeargs")
}

android {
    defaultConfig {
        applicationId = "org.alsi.android.tvlaba.mobile"

        versionCode = 1
        versionName = "1.0.1"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

    //Modules
    implementation(project(":Domain"))
    implementation(project(":PresentationTv"))
    implementation(project(":MoiDom"))

    //Activity Jetpack
    implementation(AndroidX.activity)
    implementation(AndroidX.activityKtx)

    //ExoPlayer
    implementation(Libs.exoplayer)
    implementation(Libs.exoplayerLeanback)

    //Navigation
    implementation(Libs.jetPackNavigationFragment)
    implementation(Libs.jetPackNavigationKtx)

    implementation(Libs.jodaTime)
}
