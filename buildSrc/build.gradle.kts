import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        //classpath("io.objectbox:objectbox-gradle-plugin:2.7.0") // cannot use this for precompiled scripts
        //classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.4") // cannot use this for precompiled scripts
    }
}

repositories {
    jcenter()
    google()
}

dependencies {

    implementation("com.android.tools.build:gradle:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    implementation("com.android.tools.build:gradle-api:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")

    //implementation(gradleApi())
    //implementation(localGroovy())
}

