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

        versionCode = 6
        versionName = "1.0.6"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "org.alsi.android.tvlaba.tv.TvLabATestRunner"

        applicationVariants.all {
            outputs.all {
                (this as ApkVariantOutputImpl).outputFileName =
                        "tvlaba-${name}-${versionCode}.apk"
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
            .using(project(":leanback")
        )
    }
}


dependencies {

    //Modules
    implementation(project(":Domain"))
    implementation(project(":PresentationTv"))
    implementation(project(":PresentationVod"))
    implementation(project(":MoiDom"))

    //Leanback
    implementation(project(":leanback")) // customized extract of the library from androidx
    implementation(Libs.leanbackPreferences)

    //ExoPlayer
    implementation(Libs.exoplayer)
    implementation(Libs.exoplayerLeanback)

    //Navigation: refreshVersion plugin used here, i.e. supported package references
    implementation(AndroidX.navigation.fragment)
    implementation(AndroidX.navigation.uiKtx)

    //Retrofit (to support exception testing)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitGson)
    implementation(Libs.retrofitAdapter)

    //Time
    implementation(Libs.jodaTime)

    //Progress
    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    //Instrumented testing
    androidTestImplementation(Libs.androidAnnotations)
    androidTestImplementation(Libs.espressoCore)

    androidTestImplementation(Libs.androidSupportRunner)
    androidTestImplementation(Libs.androidSupportRules)

    androidTestImplementation(Libs.jUnit)

    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.monitor)

    kaptAndroidTest(Libs.daggerCompiler)
    kaptAndroidTest(Libs.daggerProcessor)

    androidTestImplementation("com.squareup.rx.idler:rx2-idler:0.11.0")
}
