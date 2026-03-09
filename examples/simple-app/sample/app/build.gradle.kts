plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.share.sample.simple.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.share.sample.simple.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17 } }

dependencies {
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Feature modules
    implementation(projects.examples.simpleApp.sample.feature.main)
    implementation(projects.examples.simpleApp.sample.feature.onboarding)
    implementation(projects.examples.simpleApp.sample.feature.home)
    implementation(projects.examples.simpleApp.sample.feature.favorites)
    implementation(projects.examples.simpleApp.sample.feature.details)
    implementation(projects.examples.simpleApp.sample.feature.profile)
    implementation(projects.examples.simpleApp.sample.core.auth)
    implementation(projects.examples.simpleApp.sample.core.data)

    implementation(projects.library.getbackcomposeFoundation)
    implementation(projects.library.getbackcomposeActivity)
    implementation(projects.library.getbackcomposeCompose)
    implementation(projects.library.getbackcomposeCore)
    implementation(projects.library.getbackcomposeNavigationStack)
    implementation(projects.library.getbackcomposeNavigationSwitcher)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
