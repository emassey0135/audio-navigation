plugins {
  id("net.neoforged.moddev")
}
repositories {
  maven("https://maven.twelveiterations.com/repository/maven-public/")
  maven("https://maven.fzzyhmstrs.me/")
}
val neoform_version: String by project
neoForge {
  neoFormVersion = neoform_version
}
val balm_version: String by project
val fzzy_config_version: String by project
val lwjgl_version: String by project
dependencies {
  implementation("net.blay09.mods:balm-common:$balm_version")
  compileOnly("me.fzzyhmstrs:fzzy_config:$fzzy_config_version")
  compileOnly("org.lwjgl:lwjgl-opus:$lwjgl_version")
  compileOnly(project(":common")) { isTransitive = false }
}
