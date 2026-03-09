plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.share.sample.simple.feature.favorites"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.examples.shared.sample.core.data)
            implementation(projects.examples.simpleApp.sample.feature.details)
            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(projects.library.getbackcomposeNavigationStack)
            implementation(projects.library.getbackcomposeCore)

            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor3)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
