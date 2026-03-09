plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "SimpleApp"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(projects.examples.simpleApp.sample.feature.main)
            implementation(projects.examples.simpleApp.sample.feature.onboarding)
            implementation(projects.examples.simpleApp.sample.feature.home)
            implementation(projects.examples.simpleApp.sample.feature.favorites)
            implementation(projects.examples.simpleApp.sample.feature.details)
            implementation(projects.examples.simpleApp.sample.feature.profile)
            implementation(projects.examples.shared.sample.core.auth)
            implementation(projects.examples.shared.sample.core.data)

            implementation(projects.library.getbackcomposeFoundation)
            implementation(projects.library.getbackcomposeCompose)
            implementation(projects.library.getbackcomposeCore)
            implementation(projects.library.getbackcomposeNavigationStack)
            implementation(projects.library.getbackcomposeNavigationSwitcher)

            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
