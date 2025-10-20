pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    // Permite repos en el módulo si hace falta (útil para aislar el problema)
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        // redundante pero a veces ayuda en redes corporativas:
        maven { url = uri("https://maven.google.com") }
    }
}
rootProject.name = "SafetyFirst"
include(":app")
