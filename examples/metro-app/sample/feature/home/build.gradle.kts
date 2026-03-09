plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.share.sample.metro.feature.home"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.examples.metroApp.sample.core.data)
            implementation(projects.examples.metroApp.sample.feature.details)
            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(projects.library.getbackcomposeNavigationStack)
            implementation(projects.library.getbackcomposeNavigationSwitcher)
            implementation(projects.library.getbackcomposeCore)

            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor3)
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
