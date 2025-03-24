plugins {
  id("io.papermc.paperweight.userdev")
  id("io.github.dueris.eclipse.gradle")
  kotlin("plugin.serialization") version "2.1.20"
}
val sqlite_jdbc_version: String by project
dependencies {
  paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
  compileOnly("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")
}
sourceSets {
  main {
    java.srcDir("../common/src/main/java")
    kotlin.srcDir("../common/src/main/kotlin")
    resources.srcDir("../common/src/main/resources")
  }
}
eclipse {
  minecraft = "1.21.4-R0.1-SNAPSHOT"
}
paperweight {
  reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
