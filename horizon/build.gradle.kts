plugins {
  id("io.canvasmc.weaver.userdev")
  id("io.canvasmc.horizon")
  id("com.modrinth.minotaur")
}
val horizon_api_version: String by project
val paper_api_version: String by project
dependencies {
  horizon.horizonApi(horizon_api_version)
  paperweight.paperDevBundle(paper_api_version)
  includeMixinPlugin(project(":common-horizon")) { isTransitive = false }
}
horizon {
  splitPluginSourceSets()
}
val version: String by project
val mod_name: String by project
val mod_description: String by project
val mod_author: String by project
val mod_github_url: String by project
val mod_id: String by project
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
    ))
  }
}
tasks.named<ProcessResources>("processPluginResources") {
  filesMatching("paper-plugin.yml") {
    expand(mapOf(
      "version" to version,
      "mod_name" to mod_name.replace(" ", ""),
      "mod_description" to mod_description,
      "mod_author" to mod_author,
      "mod_github_url" to mod_github_url,
      "paper_api_version" to paper_api_version.replace("-.*".toRegex(), ""),
      "mod_id" to mod_id,
    ))
  }
}
val modrinth_slug: String by project
val minecraft_version: String by project
modrinth {
  projectId.set(modrinth_slug)
  versionNumber.set(version)
  versionName.set("Audio Navigation $version (Paper)")
  versionType.set("beta")
  uploadFile.set(tasks.jar)
  gameVersions.addAll(minecraft_version)
  loaders.addAll("paper", "purpur")
  changelog.set(file("../CHANGELOG.md").readText())
}
