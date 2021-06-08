import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    id("name.remal.check-dependency-updates") version "1.3.0"
}

buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    }
}

repositories {
    jcenter()
    google()
}

dependencies {

    implementation("com.android.tools.build:gradle:4.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    implementation("com.android.tools.build:gradle-api:4.1.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")
    //implementation(kotlin("stdlib"))
}
