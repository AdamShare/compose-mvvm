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

rootProject.name = "GetBackCompose"

include(":apps:sample:app")
include(":apps:sample:core:auth")
include(":apps:sample:core:data")
include(":apps:sample:feature:details")
include(":apps:sample:feature:favorites")
include(":apps:sample:feature:home")
include(":apps:sample:feature:main")
include(":apps:sample:feature:onboarding")
include(":apps:sample:feature:profile")
include(":apps:sample:integrations:main")
include(":apps:subcomponent:app")
include(":apps:subcomponent:feature:onboarding")
include(":external:foundation:coroutines")
include(":external:foundation:coroutines:test")
include(":external:lib:compose")
include(":external:lib:activity")
include(":external:lib:lifecycle")
include(":external:lib:navigation-multidisplay")
include(":external:lib:navigation-stack")
include(":external:lib:navigation-switcher")
include(":external:lib:view")
