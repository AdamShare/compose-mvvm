plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.share.sample.feature.main"
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
    ksp(libs.metro.compiler)

    implementation(projects.examples.metroApp.sample.core.auth)
    implementation(projects.examples.metroApp.sample.core.data)
    implementation(projects.examples.metroApp.sample.feature.details)
    implementation(projects.examples.metroApp.sample.feature.favorites)
    implementation(projects.examples.metroApp.sample.feature.home)
    implementation(projects.examples.metroApp.sample.feature.profile)

    implementation(projects.library.getbackcomposeFoundation)
    implementation(projects.library.getbackcomposeCompose)
    implementation(projects.library.getbackcomposeNavigationStack)
    implementation(projects.library.getbackcomposeNavigationSwitcher)
    implementation(projects.library.getbackcomposeCore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.metro.runtime)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
