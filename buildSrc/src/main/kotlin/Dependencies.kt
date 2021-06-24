object Plugins {
    
    const val androidApplication = "com.android.application"
    const val androidKotlin = "kotlin-android"
    const val androidKotlinExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val jetPackNavigation = "androidx.navigation.safeargs"
}

object Libs {

    // Kotlin
    const val kotlin =        "org.jetbrains.kotlin:kotlin-stdlib:_"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:_"

    // Annotations
    const val javaxAnnotation =     "javax.annotation:jsr250-api:_"
    const val androidAnnotations =  "androidx.annotation:annotation:_"
    const val glassfishAnnotation = "org.glassfish:javax.annotation:_"

    // Dependency Injection
    const val javaxInject =     "javax.inject:javax.inject:_"
    // !!! Cannot use last dagger version: "Could not resolve androidx.activity:activity:1.2.2. Required by:  project :Domain > com.google.dagger:dagger-android-support:2.36
    const val dagger =          "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler =  "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    const val daggerSupport =   "com.google.dagger:dagger-android-support:${Versions.dagger}"

    // RX
    const val rxJava =    "io.reactivex.rxjava2:rxjava:_"
    const val rxKotlin =  "io.reactivex.rxjava2:rxkotlin:_"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:_"

    // General
    const val jodaTime = "joda-time:joda-time:_"
    const val gson =     "com.google.code.gson:gson:_"
    const val glide =    "com.github.bumptech.glide:glide:_"
    const val timber =   "com.jakewharton.timber:timber:_"

    // okHttp
    const val okHttp =       "com.squareup.okhttp3:okhttp:_"
    const val okHttpLogger = "com.squareup.okhttp3:logging-interceptor:_"

    // Retrofit
    const val retrofit =        "com.squareup.retrofit2:retrofit:_"
    const val retrofitGson =    "com.squareup.retrofit2:converter-gson:_"
    const val retrofitScalars = "com.squareup.retrofit2:converter-scalars:_"
    const val retrofitAdapter = "com.squareup.retrofit2:adapter-rxjava2:_"

    // ObjectBox
    const val objectboxAndroid = "io.objectbox:objectbox-android:_"
    const val objectboxKotlin =  "io.objectbox:objectbox-kotlin:_"

    // Leanback: used custom copy which requires certain leanback-preference version
    const val leanback =            "androidx.leanback:leanback:${Versions.leanback}"
    const val leanbackPreferences = "androidx.leanback:leanback-preference:${Versions.leanback}"

    // Exoplayer
    const val exoplayer =         "com.google.android.exoplayer:exoplayer:_"
    const val exoplayerLeanback = "com.google.android.exoplayer:extension-leanback:_"

    // Layout & Design
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:_"
    const val recyclerView =     "androidx.recyclerview:recyclerview:_"
    const val materialDesign =   "com.google.android.material:material:_"

    const val viewBinding = "androidx.databinding:viewbinding:_"

    // JetPack Navigation
    const val jetPackNavigationFragment = "androidx.navigation:navigation-fragment-ktx:_"
    const val jetPackNavigationKtx =      "androidx.navigation:navigation-ui-ktx:_"

    // Architectural Components
    const val archCompiler =   "androidx.lifecycle:lifecycle-compiler:_"
    const val archRuntime =    "androidx.lifecycle:lifecycle-runtime:_"
    const val archExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.archExtensions}" // refreshVersions uses group version here by mistake

    // Compatibility
    const val appCompatV7 = "androidx.appcompat:appcompat:_"

    // -- Testing

    // Testing: Java
    const val jUnit = "junit:junit:_"
    const val assertJ = "org.assertj:assertj-core:_"

    // Testing: Kotlin
    const val kotlinJUnit= "org.jetbrains.kotlin:kotlin-test-junit:_"

    // Testing: Mockito
    const val mockitoKotlin =  "com.nhaarman.mockitokotlin2:mockito-kotlin:_"
    const val mockitoAndroid = "org.mockito:mockito-android:_"

    // Testing: Espresso
    const val espressoCore =    "androidx.test.espresso:espresso-core:_"
    const val espressoIntents = "androidx.test.espresso:espresso-intents:_"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:_"

    // Testing: roboelectric
    const val robolectric =  "org.robolectric:robolectric:_"

    // Testing: Android Support
    const val androidSupportRunner = "androidx.test:runner:_"
    const val androidSupportRules =  "androidx.test:rules:_"

    // Testing: ObjectBox
    const val objectboxTestLinux =   "io.objectbox:objectbox-linux:_"
    const val objectboxTestMacOS =   "io.objectbox:objectbox-macos:_"
    const val objectboxTestWindows = "io.objectbox:objectbox-windows:_"

    // Testing: Mock Web Server
    const val okHttpMockWebServer = "com.squareup.okhttp3:mockwebserver:_"

    const val archTesting = "androidx.arch.core:core-testing:_"

    const val timberUnitRule = "net.lachlanmckee:timber-junit-rule:_"
}
