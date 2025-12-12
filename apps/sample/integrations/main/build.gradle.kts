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

    implementation(projects.apps.sample.core.auth)
    implementation(projects.apps.sample.core.data)
    implementation(projects.apps.sample.feature.details)
    implementation(projects.apps.sample.feature.favorites)
    implementation(projects.apps.sample.feature.home)
    implementation(projects.apps.sample.feature.main)
    implementation(projects.apps.sample.feature.onboarding)
    implementation(projects.apps.sample.feature.profile)
    implementation(projects.external.foundation.coroutines)
    implementation(projects.external.lib.activity)
    implementation(projects.external.lib.compose)
    implementation(projects.external.lib.navigationStack)
    implementation(projects.external.lib.navigationSwitcher)
    implementation(projects.external.lib.view)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.dagger)

    testImplementation(libs.junit)
}
