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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Sample"

include(":apps:sample:app")
include(":apps:sample:feature:onboarding")
include(":apps:subcomponent:app")
include(":apps:subcomponent:feature:onboarding")
include(":external:foundation:coroutines")
include(":external:foundation:coroutines:test")
include(":external:lib:mvvm")
include(":external:lib:compose")
include(":external:lib:activity")
include(":external:lib:core")
