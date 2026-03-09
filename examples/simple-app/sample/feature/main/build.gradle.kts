plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.share.sample.simple.feature.main"
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
            implementation(projects.examples.shared.sample.core.data)
            implementation(projects.examples.simpleApp.sample.feature.details)
            implementation(projects.examples.simpleApp.sample.feature.favorites)
            implementation(projects.examples.simpleApp.sample.feature.home)
            implementation(projects.examples.simpleApp.sample.feature.profile)

            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(projects.library.getbackcomposeNavigationStack)
            implementation(projects.library.getbackcomposeNavigationSwitcher)
            implementation(projects.library.getbackcomposeCore)

            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
