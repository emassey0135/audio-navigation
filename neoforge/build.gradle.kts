plugins {
  id("dev.architectury.loom")
  id("architectury-plugin")
  id("com.gradleup.shadow")
}
repositories {
  maven {
    name = "NeoForged"
    url = uri("https://maven.neoforged.net/releases")
  }
  maven {
    name = "Kotlin for Forge"
    url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
  maven {
    name = "FzzyMaven"
    url = uri("https://maven.fzzyhmstrs.me/")
  }
}
loom {
  silentMojangMappingsLicense()
}
architectury {
  platformSetupLoomIde()
  neoForge()
}
val common: Configuration by configurations.creating
val shadowBundle: Configuration by configurations.creating
val shadow: Configuration by configurations.getting
val developmentNeoForge: Configuration by configurations.getting
configurations {
  compileOnly.configure {
    extendsFrom(common)
  }
  runtimeOnly.configure {
    extendsFrom(common)
  }
  developmentNeoForge.extendsFrom(common)
}
val minecraft_version: String by project
val neoforge_version: String by project
val kotlin_for_forge_version: String by project
val architectury_api_version: String by project
val fzzy_config_neoforge_version: String by project
val sqlite_jdbc_version: String by project
val lwjgl_version: String by project
dependencies {
  minecraft("net.minecraft:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  neoForge("net.neoforged:neoforge:$neoforge_version")
  modImplementation("dev.architectury:architectury-neoforge:$architectury_api_version")
  implementation("thedarkcolour:kotlinforforge-neoforge:$kotlin_for_forge_version")
  modImplementation("me.fzzyhmstrs:fzzy_config:$fzzy_config_neoforge_version")
  implementation("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  shadow("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  implementation("org.lwjgl:lwjgl-opus:$lwjgl_version")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-linux")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-linux-arm64")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-macos")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-macos-arm64")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-windows")
  shadow("org.lwjgl:lwjgl-opus:$lwjgl_version:natives-windows-arm64")
  common(project(":common", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common", "transformProductionNeoForge"))
  common(project(":common-client", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common-client", "transformProductionNeoForge"))
}
val version: String by project
tasks.processResources {
  filesMatching("META-INF/neoforge.mods.toml") {
    expand(mapOf("version" to version))
  }
}
tasks.shadowJar {
  configurations = listOf(shadowBundle, shadow)
  archiveClassifier.set("dev-shadow")
  exclude("org/lwjgl/*.class")
  exclude("org/lwjgl/system/")
  exclude("org/sqlite/native/*/arm/")
  exclude("org/sqlite/native/*/armv6/")
  exclude("org/sqlite/native/*/armv7/")
  exclude("org/sqlite/native/*/ppc64/")
  exclude("org/sqlite/native/*/riscv64/")
  exclude("org/sqlite/native/*/x86/")
  exclude("org/sqlite/native/FreeBSD/")
  exclude("org/sqlite/native/Linux-Android/")
  exclude("org/sqlite/native/Linux-Musl/")
}
tasks.remapJar {
  inputFile.set(tasks.shadowJar.get().archiveFile)
}
