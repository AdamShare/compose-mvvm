enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        exclusiveContent {
            forRepository {
                ivy("https://nodejs.org/dist/") {
                    name = "Node Distributions at $url"
                    patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
                    metadataSources { artifact() }
                    content { includeModule("org.nodejs", "node") }
                }
            }
            filter { includeGroup("org.nodejs") }
        }
        exclusiveContent {
            forRepository {
                ivy("https://github.com/yarnpkg/yarn/releases/download") {
                    name = "Yarn Distributions at $url"
                    patternLayout { artifact("v[revision]/[artifact]-v[revision].[ext]") }
                    metadataSources { artifact() }
                    content { includeModule("com.yarnpkg", "yarn") }
                }
            }
            filter { includeGroup("com.yarnpkg") }
        }
    }
}

rootProject.name = "GetBackCompose"

// Library modules
include(":library:getbackcompose-foundation")
include(":library:getbackcompose-foundation-test")
include(":library:getbackcompose-core")
include(":library:getbackcompose-compose")
include(":library:getbackcompose-navigation-stack")
include(":library:getbackcompose-navigation-switcher")
include(":library:getbackcompose-navigation-multidisplay")
include(":library:getbackcompose-lifecycle")
include(":library:getbackcompose-activity")

// Example shared modules - Multiplatform business logic
include(":examples:shared:sample:core:auth")
include(":examples:shared:sample:core:data")

// Example apps - Dagger (Android-only, for reference)
include(":examples:dagger-app:sample:app")
include(":examples:dagger-app:sample:core:auth")
include(":examples:dagger-app:sample:core:data")
include(":examples:dagger-app:sample:feature:details")
include(":examples:dagger-app:sample:feature:favorites")
include(":examples:dagger-app:sample:feature:home")
include(":examples:dagger-app:sample:feature:main")
include(":examples:dagger-app:sample:feature:onboarding")
include(":examples:dagger-app:sample:feature:profile")
include(":examples:dagger-app:sample:integrations:main")

// Example apps - Simple (No DI, manual wiring)
include(":examples:simple-app:sample:app")
include(":examples:simple-app:sample:desktop")
include(":examples:simple-app:sample:ios")
include(":examples:simple-app:sample:wasmJs")
include(":examples:simple-app:sample:core:auth")
include(":examples:simple-app:sample:core:data")
include(":examples:simple-app:sample:feature:details")
include(":examples:simple-app:sample:feature:favorites")
include(":examples:simple-app:sample:feature:home")
include(":examples:simple-app:sample:feature:main")
include(":examples:simple-app:sample:feature:onboarding")
include(":examples:simple-app:sample:feature:profile")

// Example apps - Metro (KMP DI)
include(":examples:metro-app:sample:app")
include(":examples:metro-app:sample:desktop")
include(":examples:metro-app:sample:ios")
include(":examples:metro-app:sample:wasmJs")
include(":examples:metro-app:sample:core:auth")
include(":examples:metro-app:sample:core:data")
include(":examples:metro-app:sample:feature:details")
include(":examples:metro-app:sample:feature:favorites")
include(":examples:metro-app:sample:feature:home")
include(":examples:metro-app:sample:feature:main")
include(":examples:metro-app:sample:feature:onboarding")
include(":examples:metro-app:sample:feature:profile")
include(":examples:metro-app:sample:integrations:main")
