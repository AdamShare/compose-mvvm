plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.getbackcompose.navigation.stack"
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

    js(IR) {
        browser()
        nodejs()
    }

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.library.getbackcomposeCore)
            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.compose.ui.backhandler)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
