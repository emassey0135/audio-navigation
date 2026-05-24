import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
  java
  kotlin("jvm") version "2.3.21"
  kotlin("plugin.serialization") version "2.3.21" apply false
  id("com.gradleup.shadow") version "9.4.1" apply false
  id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.141" apply false
  id("io.canvasmc.weaver.userdev") version "2.4.4" apply false
  id("io.canvasmc.horizon") version "1.0.2" apply false
  id("com.modrinth.minotaur") version "2.+" apply false
  id("io.github.themrmilchmann.curseforge-publish") version "0.9.0" apply false
}
val maven_group: String by project
val mod_version: String by project
allprojects {
  group = maven_group
  version = mod_version
}
val archives_name: String by project
val name: String by project
subprojects {
  apply(plugin = "maven-publish")
  apply(plugin = "java")
  apply(plugin = "kotlin")
  base {
    archivesName = "$archives_name-$name"
  }
  java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
  }
  tasks.withType<JavaCompile> {
    options.release.set(25)
  }
  kotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
  }
}
