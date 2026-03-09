plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.share.sample.metro.core.data.di"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(projects.examples.shared.sample.core.data)
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
