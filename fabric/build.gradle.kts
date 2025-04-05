plugins {
  id("dev.architectury.loom")
  id("architectury-plugin")
  id("com.gradleup.shadow")
}
repositories {
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
  fabric()
}
val common: Configuration by configurations.creating
val shadowBundle: Configuration by configurations.creating
val shadow: Configuration by configurations.getting
val developmentFabric: Configuration by configurations.getting
configurations {
  compileOnly.configure {
    extendsFrom(common)
  }
  runtimeOnly.configure {
    extendsFrom(common)
  }
  developmentFabric.extendsFrom(common)
}
val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project
val fabric_kotlin_version: String by project
val architectury_api_version: String by project
val fzzy_config_version: String by project
val sqlite_jdbc_version: String by project
val lwjgl_version: String by project
val kotlinx_serialization_fabric_version: String by project
dependencies {
  minecraft("net.minecraft:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
  modImplementation("dev.architectury:architectury-fabric:$architectury_api_version")
  modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
  modImplementation("me.fzzyhmstrs:fzzy_config:$fzzy_config_version")
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
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_fabric_version")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_fabric_version")
  common(project(":common", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common", "transformProductionFabric"))
  common(project(":common-fabric-neoforge", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common-fabric-neoforge", "transformProductionFabric"))
  common(project(":common-client", "namedElements")) { isTransitive = false }
  shadowBundle(project(":common-client", "transformProductionFabric"))
}
val version: String by project
val mod_id: String by project
val mod_name: String by project
val mod_description: String by project
val mod_icon: String by project
val mod_author: String by project
val mod_github_url: String by project
val mod_license: String by project
val common_mixins_file: String by project
val minecraft_access_version: String by project
tasks.processResources {
  filesMatching("fabric.mod.json") {
    expand(mapOf(
      "version" to version,
      "mod_id" to mod_id,
      "mod_name" to mod_name,
      "mod_description" to mod_description,
      "mod_icon" to mod_icon,
      "mod_author" to mod_author,
      "mod_github_url" to mod_github_url,
      "mod_license" to mod_license,
      "common_mixins_file" to common_mixins_file,
      "fabric_loader_version" to fabric_loader_version,
      "minecraft_version" to minecraft_version,
      "architectury_api_version" to architectury_api_version,
      "fabric_api_version" to fabric_api_version.replace("\\+.*".toRegex(), ""),
      "fabric_kotlin_version" to fabric_kotlin_version.replace("\\+.*".toRegex(), ""),
      "fzzy_config_version" to fzzy_config_version.replace("\\+.*".toRegex(), ""),
      "minecraft_access_version" to minecraft_access_version,
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
