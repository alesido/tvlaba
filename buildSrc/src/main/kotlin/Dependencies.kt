object Plugins {
    
    const val androidApplication = "com.android.application"
    const val androidKotlin = "kotlin-android"
    const val androidKotlinExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val jetPackNavigation = "androidx.navigation.safeargs"
}

object Libs {

    // Kotlin
    const val kotlin =        "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinReflect}"

    // Annotations
    const val javaxAnnotation =     "javax.annotation:jsr250-api:${Versions.javaxAnnotation}"
    const val androidAnnotations =  "androidx.annotation:annotation:${Versions.androidAnnotation}"
    const val glassfishAnnotation = "org.glassfish:javax.annotation:${Versions.glassfishAnnotation}"

    // Dependency Injection
    const val javaxInject =     "javax.inject:javax.inject:${Versions.javaxInject}"
    const val dagger =          "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler =  "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    const val daggerSupport =   "com.google.dagger:dagger-android-support:${Versions.dagger}"

    // RX
    const val rxJava =    "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin =  "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    // General
    const val jodaTime = "joda-time:joda-time:${Versions.jodaTime}"
    const val gson =     "com.google.code.gson:gson:${Versions.gson}"
    const val glide =    "com.github.bumptech.glide:glide:${Versions.glide}"
    const val timber =   "com.jakewharton.timber:timber:${Versions.timber}"

    // okHttp
    const val okHttp =       "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpLogger = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    // Retrofit
    const val retrofit =        "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson =    "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitScalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    const val retrofitAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"

    // ObjectBox
    const val objectboxAndroid = "io.objectbox:objectbox-android:${Versions.objectbox}"
    const val objectboxKotlin =  "io.objectbox:objectbox-kotlin:${Versions.objectbox}"

    // Leanback
    const val leanback =            "androidx.leanback:leanback:${Versions.leanback}"
    const val leanbackPreferences = "androidx.leanback:leanback-preference:${Versions.leanback}"

    // Exoplayer
    const val exoplayer =         "com.google.android.exoplayer:exoplayer:${Versions.exoplayer}"
    const val exoplayerLeanback = "com.google.android.exoplayer:extension-leanback:${Versions.exoplayerLeanback}"

    // Layout & Design
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val recyclerView =     "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val materialDesign =   "com.google.android.material:material:${Versions.materialDesign}"

    // JetPack Navigation
    const val jetPackNavigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.jetPackNavigation}"
    const val jetPackNavigationKtx =      "androidx.navigation:navigation-ui-ktx:${Versions.jetPackNavigation}"

    // Architectural Components
    const val archCompiler =   "androidx.lifecycle:lifecycle-compiler:${Versions.archComponents}"
    const val archRuntime =    "androidx.lifecycle:lifecycle-runtime:${Versions.archComponents}"
    const val archExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.archExtensions}"

    // Compatibility
    const val appCompatV7 = "androidx.appcompat:appcompat:${Versions.appCompatV7}"

    // -- Testing

    // Testing: Java
    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val assertJ = "org.assertj:assertj-core:${Versions.assertJ}"

    // Testing: Kotlin
    const val kotlinJUnit= "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"

    // Testing: Mockito
    const val mockitoKotlin =  "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoAndroid = "org.mockito:mockito-android:${Versions.mockitoAndroid}"

    // Testing: Espresso
    const val espressoCore =    "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"

    // Testing: Android Support
    const val androidSupportRunner = "androidx.test:runner:${Versions.androidSupportRunner}"
    const val androidSupportRules =  "androidx.test:rules:${Versions.androidSupportRules}"

    // Testing: ObjectBox
    const val objectboxTestLinux =   "io.objectbox:objectbox-linux:${Versions.objectbox}"
    const val objectboxTestMacOS =   "io.objectbox:objectbox-macos:${Versions.objectbox}"
    const val objectboxTestWindows = "io.objectbox:objectbox-windows:${Versions.objectbox}"

    // Testing: Mock Web Server
    const val okHttpMockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okHttpMockWebServer}"

    const val archTesting = "androidx.arch.core:core-testing:${Versions.archTesting}"

    const val timberUnitRule = "net.lachlanmckee:timber-junit-rule:${Versions.timberUnitRule}"
}
