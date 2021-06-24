// Top-level build file where you can add configuration options common to all sub-projects/modules.

@Suppress("JcenterRepositoryObsolete")
buildscript {

    val kotlinVersion = "1.4.31"
    val androidGradlePluginVersion = "4.2.1"
    val objectboxVersion = "2.7.0"
    val navigationVersion = "2.3.0"

    repositories {
        google()
        jcenter() // left read only forever
        mavenCentral()
        maven("https://dl.google.com/dl/android/maven2")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$androidGradlePluginVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter() // left read only forever
        mavenCentral()
    }
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}