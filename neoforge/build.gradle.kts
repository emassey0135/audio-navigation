plugins {
  id("dev.architectury.loom")
  id("architectury-plugin")
  id("com.gradleup.shadow")
  id("com.modrinth.minotaur")
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
val kotlinx_serialization_neoforge_version: String by project
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
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_neoforge_version")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_neoforge_version")
  common(project(":common", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common", "transformProductionNeoForge"))
  common(project(":common-fabric-neoforge", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common-fabric-neoforge", "transformProductionNeoForge"))
  common(project(":common-client", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common-client", "transformProductionNeoForge"))
}
val version: String by project
val mod_github_url: String by project
val mod_license: String by project
val mod_id: String by project
val mod_name: String by project
val mod_author: String by project
val mod_description: String by project
val mod_icon: String by project
val minecraft_access_version: String by project
val common_mixins_file: String by project
tasks.processResources {
  filesMatching("META-INF/neoforge.mods.toml") {
    expand(mapOf(
      "version" to version,
      "mod_github_url" to mod_github_url,
      "mod_license" to mod_license,
      "mod_id" to mod_id,
      "mod_name" to mod_name,
      "mod_author" to mod_author,
      "mod_description" to mod_description,
      "mod_icon" to mod_icon,
      "neoforge_version" to neoforge_version.replace("\\.[^\\.]*$".toRegex(), ""),
      "minecraft_version" to minecraft_version,
      "architectury_api_version" to architectury_api_version,
      "kotlin_for_forge_version" to kotlin_for_forge_version,
      "fzzy_config_neoforge_version" to fzzy_config_neoforge_version.replace("\\+.*".toRegex(), ""),
      "minecraft_access_version" to minecraft_access_version,
      "common_mixins_file" to common_mixins_file,
    ))
  }
}
tasks.shadowJar {
  configurations = listOf(shadowBundle, shadow)
  archiveClassifier.set("dev-shadow")
  exclude("kotlin/")
  exclude("kotlinx/serialization/*.class")
  exclude("kotlinx/serialization/builtins/")
  exclude("kotlinx/serialization/descriptors/")
  exclude("kotlinx/serialization/encoding/")
  exclude("kotlinx/serialization/internal/")
  exclude("kotlinx/serialization/modules/")
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
val modrinth_slug: String by project
modrinth {
  projectId.set(modrinth_slug)
  versionNumber.set(version)
  versionName.set("Audio Navigation $version (NeoForge)")
  versionType.set("beta")
  uploadFile.set(tasks.remapJar)
  gameVersions.addAll(minecraft_version)
  loaders.add("neoforge")
  changelog.set(file("../CHANGELOG.md").readText())
  dependencies {
    required.project("architectury-api")
    required.project("kotlin-for-forge")
    required.project("fzzy-config")
    optional.project("minecraft-access")
  }
}
