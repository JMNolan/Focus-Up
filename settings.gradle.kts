pluginManagement {
    repositories {
        google()
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

rootProject.name = "Focus-Up"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":feature:timer")
include(":feature:stickerbook")

