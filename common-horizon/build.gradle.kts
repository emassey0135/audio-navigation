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
