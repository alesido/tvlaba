plugins {
    id("android-library-module")
    id("io.objectbox")
}

dependencies {

    api(project(":Domain"))
    api(project(":Data"))
    api(project(":DataTv"))

    implementation(Libs.kotlin)
    implementation(Libs.rxKotlin)
    implementation(Libs.rxAndroid)
    implementation(Libs.kotlinReflect)

    //implementation localDependencies.appCompatV7

    dependencyInjection()

    implementation(Libs.objectboxAndroid)
    implementation(Libs.objectboxKotlin)

    testImplementation(Libs.jUnit)
    testImplementation(Libs.kotlinJUnit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.assertJ)

    testImplementation(Libs.objectboxTestLinux)
    testImplementation(Libs.objectboxTestMacOS)
    testImplementation(Libs.objectboxTestWindows)
}
