plugins {
  id("com.gradleup.shadow")
  id("io.papermc.paperweight.userdev")
}
val paper_api_version: String by project
val sqlite_jdbc_version: String by project
val kotlinx_serialization_included_version: String by project
dependencies {
  paperweight.paperDevBundle(paper_api_version)
  implementation(project(":common-paper"))
  shadow(project(":common-paper"))
  implementation("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
}
val version: String by project
tasks.processResources {
  filesMatching("paper-plugin.yml") {
    expand(mapOf("version" to version))
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
