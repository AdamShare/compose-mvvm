plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.share.sample.simple.feature.profile"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.examples.shared.sample.core.auth)
            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(projects.library.getbackcomposeCore)

            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
