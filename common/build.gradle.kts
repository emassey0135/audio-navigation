plugins {
  id("net.neoforged.moddev")
  kotlin("plugin.serialization")
}
val neoform_version: String by project
neoForge {
  neoFormVersion = neoform_version
}
val mixin_version: String by project
val sqlite_jdbc_version: String by project
val kotlinx_serialization_included_version: String by project
dependencies {
  compileOnly("org.spongepowered:mixin:$mixin_version")
  compileOnly("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinx_serialization_included_version")
}
