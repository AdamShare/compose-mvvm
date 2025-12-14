plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.share.sample.integrations.main"
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

    implementation(projects.examples.daggerApp.sample.core.auth)
    implementation(projects.examples.daggerApp.sample.core.data)
    implementation(projects.examples.daggerApp.sample.feature.details)
    implementation(projects.examples.daggerApp.sample.feature.favorites)
    implementation(projects.examples.daggerApp.sample.feature.home)
    implementation(projects.examples.daggerApp.sample.feature.main)
    implementation(projects.examples.daggerApp.sample.feature.onboarding)
    implementation(projects.examples.daggerApp.sample.feature.profile)
    implementation(projects.library.getbackcomposeFoundation)
    implementation(projects.library.getbackcomposeActivity)
    implementation(projects.library.getbackcomposeCompose)
    implementation(projects.library.getbackcomposeNavigationStack)
    implementation(projects.library.getbackcomposeNavigationSwitcher)
    implementation(projects.library.getbackcomposeCore)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.dagger)

    testImplementation(libs.junit)
}
