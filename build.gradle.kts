import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
  java
  kotlin("jvm") version "2.2.20"
  kotlin("plugin.serialization") version "2.2.20" apply false
  id("dev.architectury.loom") version "1.11-SNAPSHOT" apply false
  id("architectury-plugin") version "3.4-SNAPSHOT"
  id("com.gradleup.shadow") version "9.1.0" apply false
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.18" apply false
  id("io.github.dueris.eclipse.gradle") version "1.2.3" apply false
  id("com.modrinth.minotaur") version "2.+" apply false
  id("io.github.themrmilchmann.curseforge-publish") version "0.8.0" apply false
}
val minecraft_version: String by project
architectury {
  minecraft = minecraft_version
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  tasks.withType<JavaCompile> {
    options.release.set(21)
  }
  kotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
  }
}
