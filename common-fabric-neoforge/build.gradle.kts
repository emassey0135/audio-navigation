plugins {
  id("dev.architectury.loom")
  id("architectury-plugin")
}
repositories {
  maven {
    name = "FzzyMaven"
    url = uri("https://maven.fzzyhmstrs.me/")
  }
}
loom {
  silentMojangMappingsLicense()
}
architectury {
  common(listOf("fabric", "neoforge"))
}
val minecraft_version: String by project
val fzzy_config_version: String by project
dependencies {
  minecraft("net.minecraft:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  modCompileOnly("me.fzzyhmstrs:fzzy_config:$fzzy_config_version")
  compileOnly(project(":common", "namedElements")) { isTransitive = false }
}
