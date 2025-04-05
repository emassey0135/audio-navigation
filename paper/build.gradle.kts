plugins {
  id("com.gradleup.shadow")
  id("io.papermc.paperweight.userdev")
}
val paper_api_version: String by project
val kotlinx_serialization_included_version: String by project
dependencies {
  paperweight.paperDevBundle(paper_api_version)
  implementation(project(":common-paper"))
  shadow(project(":common-paper"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
}
val version: String by project
val mod_name: String by project
val mod_description: String by project
val mod_author: String by project
val mod_github_url: String by project
val mod_id: String by project
val common_mixins_file: String by project
tasks.processResources {
  filesMatching("paper-plugin.yml") {
    expand(mapOf(
      "version" to version,
      "mod_name" to mod_name.replace(" ", ""),
      "mod_description" to mod_description,
      "mod_author" to mod_author,
      "mod_github_url" to mod_github_url,
      "paper_api_version" to paper_api_version.replace("-.*".toRegex(), ""),
      "mod_id" to mod_id,
      "common_mixins_file" to common_mixins_file,
    ))
  }
}
tasks.build {
  dependsOn(tasks.shadowJar)
}
tasks.jar {
  enabled = false
}
paperweight {
  reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
