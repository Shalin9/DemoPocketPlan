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
    repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io") // ✅ Required for GitHub-based dependencies
}

}


rootProject.name = "PocketPlanPOE"
include(":app")
