import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

@Suppress("JcenterRepositoryObsolete")
buildscript {

    repositories {
        google()
        jcenter() // left read only forever
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    }
}

repositories {
    google()
    jcenter() // left read only forever
    mavenCentral()
}

dependencies {

    implementation("com.android.tools.build:gradle:4.2.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    implementation("com.android.tools.build:gradle-api:4.2.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")
    //implementation(kotlin("stdlib"))
}
