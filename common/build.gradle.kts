plugins {
  id("dev.architectury.loom")
  id("architectury-plugin")
  kotlin("plugin.serialization")
}
loom {
  silentMojangMappingsLicense()
}
architectury {
  common(listOf("fabric", "neoforge"))
}
val minecraft_version: String by project
val fabric_loader_version: String by project
val sqlite_jdbc_version: String by project
dependencies {
  minecraft("net.minecraft:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  modCompileOnly("net.fabricmc:fabric-loader:$fabric_loader_version")
  compileOnly("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")
}
