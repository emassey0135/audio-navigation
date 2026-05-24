pluginManagement {
  repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://files.minecraftforge.net/maven/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.canvasmc.io/public/")
    gradlePluginPortal()
  }
}
rootProject.name = "audio_navigation"
include("common")
include("common-client")
include("common-fabric-neoforge")
include("common-horizon")
include("fabric")
include("horizon")
include("neoforge")
