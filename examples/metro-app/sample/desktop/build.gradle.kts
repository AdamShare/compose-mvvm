plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(projects.examples.metroApp.sample.integrations.main)
                implementation(projects.examples.shared.sample.core.auth)
                implementation(projects.examples.shared.sample.core.data)

                implementation(projects.library.getbackcomposeFoundation)
                implementation(projects.library.getbackcomposeCompose)
                implementation(projects.library.getbackcomposeCore)
                implementation(projects.library.getbackcomposeNavigationStack)
                implementation(projects.library.getbackcomposeNavigationSwitcher)

                implementation(compose.material3)
                implementation(libs.metro.runtime)
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
