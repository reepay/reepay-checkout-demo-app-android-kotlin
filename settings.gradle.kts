rootProject.name = "Reepay Checkout Kotlin Demo App"
include(":ReepayCheckoutSheetDemo")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Option 1. Load Checkout SDK dependency from Jitpack
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

// Option 2. Load project locally
// https://discuss.gradle.org/t/add-local-project-dependency/27021
//include(":checkout")
//project(":checkout").projectDir = file("/PATH/TO/reepay-android-checkout-sheet/checkout")