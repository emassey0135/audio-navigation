import io.github.themrmilchmann.gradle.publish.curseforge.ChangelogFormat
import io.github.themrmilchmann.gradle.publish.curseforge.GameVersion
import io.github.themrmilchmann.gradle.publish.curseforge.ReleaseType
plugins {
  id("net.neoforged.moddev")
  id("com.gradleup.shadow")
  id("com.modrinth.minotaur")
  id("io.github.themrmilchmann.curseforge-publish")
}
repositories {
  mavenLocal()
  maven("https://maven.neoforged.net/releases")
  maven("https://repo.nyon.dev/releases/")
  maven("https://maven.twelveiterations.com/repository/maven-public/")
  maven("https://maven.fzzyhmstrs.me/")
}
val neoforge_version: String by project
neoForge {
  version = neoforge_version
  mods {
    create("audio_navigation") {
      sourceSet(sourceSets.main.get())
    }
  }
}
val kotlin_lang_forge_version: String by project
val balm_version: String by project
val fzzy_config_neoforge_version: String by project
val sqlite_jdbc_version: String by project
val lwjgl_version: String by project
val whisprs_version: String by project
val kotlinx_serialization_neoforge_version: String by project
dependencies {
  implementation("dev.nyon:KotlinLangForge:$kotlin_lang_forge_version")
  implementation("net.blay09.mods:balm-neoforge:$balm_version")
  implementation("me.fzzyhmstrs:fzzy_config:$fzzy_config_neoforge_version")
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
  implementation("org.mcaccess:whisprs:$whisprs_version")
  shadow("org.mcaccess:whisprs:$whisprs_version")
  implementation(project(":common")) { isTransitive = false }
  shadow(project(":common")) { isTransitive = false }
  implementation(project(":common-fabric-neoforge")) { isTransitive = false }
  shadow(project(":common-fabric-neoforge")) { isTransitive = false }
  implementation(project(":common-client")) { isTransitive = false }
  shadow(project(":common-client")) { isTransitive = false }
}
val version: String by project
val mod_github_url: String by project
val mod_license: String by project
val mod_id: String by project
val mod_name: String by project
val mod_author: String by project
val mod_description: String by project
val mod_icon: String by project
val minecraft_version: String by project
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
      "balm_version" to balm_version,
      "fzzy_config_neoforge_version" to fzzy_config_neoforge_version.replace("\\+.*".toRegex(), ""),
      "minecraft_access_version" to minecraft_access_version,
      "common_mixins_file" to common_mixins_file,
    ))
  }
}
val shadow: Configuration by configurations.getting
tasks.shadowJar {
  configurations = listOf(shadow)
  archiveClassifier.set("")
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
tasks.jar {
  enabled = false
}
tasks.build {
  dependsOn(tasks.shadowJar)
}
val modrinth_slug: String by project
modrinth {
  projectId.set(modrinth_slug)
  versionNumber.set(version)
  versionName.set("Audio Navigation $version (NeoForge)")
  versionType.set("beta")
  uploadFile.set(tasks.shadowJar)
  gameVersions.addAll(minecraft_version)
  loaders.add("neoforge")
  changelog.set(file("../CHANGELOG.md").readText())
  dependencies {
    required.project("kotlin-lang-forge")
    required.project("balm")
    required.project("fzzy-config")
    optional.project("minecraft-access")
  }
}
val curseforge_id: String by project
curseforge {
  apiToken = System.getenv("CURSEFORGE_TOKEN")
  publications {
    register("neoforge") {
      projectId = curseforge_id
      gameVersions.add(GameVersion("minecraft-26-1", minecraft_version.replace(".", "-")))
      gameVersions.add(GameVersion("modloader", "neoforge"))
      gameVersions.add(GameVersion("environment", "client"))
      gameVersions.add(GameVersion("environment", "server"))
      javaVersions.add(JavaVersion.VERSION_25)
      artifacts.register("main") {
        displayName = "Audio Navigation $version (NeoForge)"
        releaseType = ReleaseType.BETA
        changelog {
          format = ChangelogFormat.MARKDOWN
          from(file("../CHANGELOG.md"))
        }
        relations {
          requiredDependency("kotlin-lang-forge")
          requiredDependency("balm")
          requiredDependency("fzzy-config")
          optionalDependency("blind-accessibility")
        }
        from(tasks.shadowJar)
      }
    }
  }
}
