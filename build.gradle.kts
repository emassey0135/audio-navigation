plugins {
  java
  kotlin("jvm") version "2.1.10"
  id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
  id("architectury-plugin") version "3.4-SNAPSHOT"
  id("com.gradleup.shadow") version "8.3.6" apply false
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.16" apply false
  id("io.github.dueris.eclipse.gradle") version "1.2.3" apply false
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
  kotlin.target.compilations.all {
    kotlinOptions {
      jvmTarget = "21"
    }
  }
}
