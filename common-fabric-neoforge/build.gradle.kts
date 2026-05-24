plugins {
  id("net.neoforged.moddev")
}
repositories {
  maven("https://maven.fzzyhmstrs.me/")
}
val neoform_version: String by project
neoForge {
  neoFormVersion = neoform_version
}
val fzzy_config_version: String by project
dependencies {
  compileOnly("me.fzzyhmstrs:fzzy_config:$fzzy_config_version")
  compileOnly(project(":common")) { isTransitive = false }
}
