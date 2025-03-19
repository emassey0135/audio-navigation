package dev.emassey0135.audionavigation.client.util

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils

object Library {
  private val IS_X86_64 = SystemUtils.OS_ARCH=="amd64" || SystemUtils.OS_ARCH=="x86_64"
  private val IS_AARCH64 = SystemUtils.OS_ARCH=="aarch64"
  private val libraryVersion = "0.3.0"
  private val baseLibraryName = "audio_navigation_tts_$libraryVersion"
  val libraryName = when {
    SystemUtils.IS_OS_LINUX && IS_X86_64 -> "lib${baseLibraryName}_x86_64.so"
    SystemUtils.IS_OS_LINUX && IS_AARCH64 -> "lib${baseLibraryName}_aarch64.so"
    SystemUtils.IS_OS_WINDOWS && IS_X86_64 -> "${baseLibraryName}_x86_64.dll"
    SystemUtils.IS_OS_WINDOWS && IS_AARCH64 -> "${baseLibraryName}_aarch64.dll"
    SystemUtils.IS_OS_MAC && IS_X86_64 -> "lib${baseLibraryName}_x86_64.dylib"
    SystemUtils.IS_OS_MAC && IS_AARCH64 -> "lib${baseLibraryName}_aarch64.dylib"
    else -> error("The current operating system or CPU architecture is not supported by this mod.")
  }
  fun downloadLibrary() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/$libraryVersion/$libraryName").toURL(), File(libraryName))
    downloadAndExtractEspeakNGData()
  }
  fun downloadAndExtractEspeakNGData() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/$libraryVersion/espeak-ng-data.zip").toURL(), File("espeak-ng-data.zip"))
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
