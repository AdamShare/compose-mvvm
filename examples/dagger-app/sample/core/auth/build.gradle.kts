plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.share.sample.dagger.core.auth.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17 } }

dependencies {
    ksp(libs.daggerCompiler)

    // Use shared multiplatform auth module for business logic
    api(projects.examples.shared.sample.core.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.dagger)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
