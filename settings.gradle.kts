pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.autonomousapps.build-health") version "2.12.0"
        id("com.android.application") apply false
        id("org.jetbrains.kotlin.android") apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

buildCache {
    local {
        isEnabled = true
    }
}

include(":app")
rootProject.name = "Portal Usuario"
