package dev.emassey0135.audionavigation

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import dev.emassey0135.audionavigation.UnzipUtility

object Library {
  val libraryName = when {
    SystemUtils.IS_OS_LINUX && SystemUtils.OS_ARCH == "amd64" -> "libaudio_navigation_tts_x86_64.so"
    SystemUtils.IS_OS_LINUX && SystemUtils.OS_ARCH == "aarch64" -> "libaudio_navigation_tts_aarch64.so"
    SystemUtils.IS_OS_WINDOWS && SystemUtils.OS_ARCH == "amd64" -> "audio_navigation_tts_x86_64.dll"
    SystemUtils.IS_OS_WINDOWS && SystemUtils.OS_ARCH == "aarch64" -> "audio_navigation_tts_aarch64.dll"
    SystemUtils.IS_OS_MAC && SystemUtils.OS_ARCH == "amd64" -> "libaudio_navigation_tts_x86_64.dylib"
    SystemUtils.IS_OS_MAC && SystemUtils.OS_ARCH == "aarch64" -> "libaudio_navigation_tts_aarch64.dylib"
    else -> error("The current operating system or CPU architecture is not supported by this mod.")
  }
  fun downloadLibrary() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/0.1.0/$libraryName").toURL(), File(libraryName))
  }
  fun downloadAndExtractEspeakNGData() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/0.1.0/espeak-ng-data.zip").toURL(), File("espeak-ng-data.zip"))
    if (Files.exists(Paths.get("espeak-ng-data")))
      FileUtils.deleteDirectory(File("espeak-ng-data"))
    UnzipUtility().unzip("espeak-ng-data.zip", ".")
    FileUtils.delete(File("espeak-ng-data.zip"))
  }
  fun initialize() {
    if (!Files.exists(Paths.get(libraryName)))
      downloadLibrary()
    if (!Files.exists(Paths.get("espeak-ng-data")))
      downloadAndExtractEspeakNGData()
  }
}
