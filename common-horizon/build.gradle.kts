plugins {
  id("io.canvasmc.weaver.userdev")
  id("io.canvasmc.horizon")
  kotlin("plugin.serialization")
}
val horizon_api_version: String by project
val paper_api_version: String by project
val sqlite_jdbc_version: String by project
val kotlinx_serialization_included_version: String by project
dependencies {
  horizon.horizonApi(horizon_api_version)
  paperweight.paperDevBundle(paper_api_version)
  compileOnly("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  includeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
}
sourceSets {
  main {
    java.srcDir("../common/src/main/java")
    kotlin.srcDir("../common/src/main/kotlin")
    resources.srcDir("../common/src/main/resources")
  }
}
val version: String by project
val mod_id: String by project
val mod_name: String by project
val mod_description: String by project
val mod_author: String by project
val minecraft_version: String by project
val common_mixins_file: String by project
tasks.processResources {
  filesMatching("horizon.plugin.json") {
    expand(mapOf(
      "version" to version,
      "mod_id" to mod_id,
      "mod_name" to mod_name.replace(" ", ""),
      "mod_description" to mod_description,
      "mod_author" to mod_author,
      "minecraft_version" to minecraft_version,
      "common_mixins_file" to common_mixins_file,
    ))
  }
}
