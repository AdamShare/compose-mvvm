plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // Feature modules
                implementation(projects.examples.simpleApp.sample.feature.main)
                implementation(projects.examples.simpleApp.sample.feature.onboarding)
                implementation(projects.examples.simpleApp.sample.feature.home)
                implementation(projects.examples.simpleApp.sample.feature.favorites)
                implementation(projects.examples.simpleApp.sample.feature.details)
                implementation(projects.examples.simpleApp.sample.feature.profile)

                // Core modules (shared)
                implementation(projects.examples.shared.sample.core.auth)
                implementation(projects.examples.shared.sample.core.data)

                // Library modules
                implementation(projects.library.getbackcomposeFoundation)
                implementation(projects.library.getbackcomposeCompose)
                implementation(projects.library.getbackcomposeCore)
                implementation(projects.library.getbackcomposeNavigationStack)
                implementation(projects.library.getbackcomposeNavigationSwitcher)

                implementation(compose.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.share.sample.desktop.MainKt"
    }
}
