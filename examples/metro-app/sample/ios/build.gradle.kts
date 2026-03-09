plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "MetroApp"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(projects.examples.metroApp.sample.integrations.main)
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
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
