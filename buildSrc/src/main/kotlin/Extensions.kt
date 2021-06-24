import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.dependencyInjection() {
    "implementation"(Libs.javaxInject)
    "implementation"(Libs.dagger)
    "implementation"(Libs.daggerSupport)
    "kapt"(Libs.daggerCompiler)
    "kapt"(Libs.daggerProcessor)
}

fun DependencyHandlerScope.annotations() {
    "implementation"(Libs.javaxAnnotation)
    "implementation"(Libs.androidAnnotations)
    "compileOnly"(Libs.glassfishAnnotation)
}

fun DependencyHandlerScope.jetPackNavigation() {
    "implementation"(Libs.jetPackNavigationFragment)
    "implementation"(Libs.jetPackNavigationKtx)
}

fun DependencyHandlerScope.archComponents() {
    "implementation"(Libs.archRuntime)
    "implementation"(Libs.archExtensions)
    "implementation"(Libs.archCompiler)
}