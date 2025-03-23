pluginManagement {
  repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    gradlePluginPortal()
  }
}
rootProject.name = "audio_navigation"
include("common")
include("common-client")
include("common-paper")
include("fabric")
include("neoforge")
include("paper")
