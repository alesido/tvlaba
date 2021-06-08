//
// Laboratory project for Set Top Box & Smart TV, i.e. big screen
//
import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    // custom precompiled plugin for conventional part of the build configuration
    id("ui-app-module-conventions")
    // JetPack's navigation plugin, cannot provide it via the precompiled plugin (?)
    id("androidx.navigation.safeargs")
}

android {

    defaultConfig {

        applicationId = "org.alsi.android.tvlaba.tv"

        versionCode = 3
        versionName = "1.0.3"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        applicationVariants.all {
            outputs.all {
                (this as ApkVariantOutputImpl).outputFileName =
                        "tvlaba-${flavorName}-${name}-${versionCode}.apk"
            }
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = "tvlaba"
            keyPassword = "tvlaba"
            storeFile = file("../tvlaba.keystore")
            storePassword = "tvlaba"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(
            module("androidx.leanback:leanback"))
            .because("using customized copy of the leanback library")
            .with(project(":leanback")
        )
    }
}


dependencies {

    //Modules
    implementation(project(":Domain"))
    implementation(project(":PresentationTv"))
    implementation(project(":MoiDom"))

    //Leanback
    implementation(project(":leanback")) // customized extract of the library from androidx
    implementation(Libs.leanbackPreferences)

    //ExoPlayer
    implementation(Libs.exoplayer)
    implementation(Libs.exoplayerLeanback)

    //Navigation
    implementation(AndroidX.Navigation.fragment)
    implementation(AndroidX.Navigation.uiKtx)
}
