plugins {
  id("io.papermc.paperweight.userdev")
  id("io.github.dueris.eclipse.gradle")
  kotlin("plugin.serialization")
}
val paper_api_version: String by project
val sqlite_jdbc_version: String by project
val kotlinx_serialization_included_version: String by project
dependencies {
  paperweight.paperDevBundle(paper_api_version)
  compileOnly("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
}
sourceSets {
  main {
    java.srcDir("../common/src/main/java")
    kotlin.srcDir("../common/src/main/kotlin")
    resources.srcDir("../common/src/main/resources")
  }
}
eclipse {
  minecraft = paper_api_version
}
paperweight {
  reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
