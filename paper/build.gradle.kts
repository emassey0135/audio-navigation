plugins {
  id("com.gradleup.shadow")
  id("io.papermc.paperweight.userdev")
}
val sqlite_jdbc_version: String by project
dependencies {
  paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
  implementation(project(":common-paper"))
  shadow(project(":common-paper"))
  implementation("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  shadow("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")
}
val version: String by project
tasks.processResources {
  filesMatching("paper-plugin.yml") {
    expand(mapOf("version" to version))
  }
}
tasks.shadowJar {
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
tasks.build {
  dependsOn(tasks.shadowJar)
}
tasks.jar {
  enabled = false
}
paperweight {
  reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
