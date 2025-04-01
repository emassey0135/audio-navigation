package dev.emassey0135.audionavigation.client.util

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils

object Library {
  private enum class Arch {
    AARCH64, X86_64
  }
  private val arch = when {
    SystemUtils.OS_ARCH=="amd64" || SystemUtils.OS_ARCH=="x86_64" -> Arch.X86_64
    SystemUtils.OS_ARCH=="aarch64" -> Arch.AARCH64
    else -> error("The current CPU architecture is not supported by Audio Navigation")
  }
  private enum class OS {
    LINUX, MAC, WINDOWS
  }
  private val os = when {
    SystemUtils.IS_OS_LINUX -> OS.LINUX
    SystemUtils.IS_OS_MAC -> OS.MAC
    SystemUtils.IS_OS_WINDOWS -> OS.WINDOWS
    else -> error("The current operating system is not supported by Audio Navigation")
  }
  private val libraryVersion = "0.3.1"
  private val baseLibraryName = "audio_navigation_tts_$libraryVersion"
  val libraryName = when (Pair(os, arch)) {
    Pair(OS.LINUX, Arch.X86_64) -> "lib${baseLibraryName}_x86_64.so"
    Pair(OS.LINUX, Arch.AARCH64) -> "lib${baseLibraryName}_aarch64.so"
    Pair(OS.MAC, Arch.X86_64) -> "lib${baseLibraryName}_x86_64.dylib"
    Pair(OS.MAC, Arch.AARCH64) -> "lib${baseLibraryName}_aarch64.dylib"
    Pair(OS.WINDOWS, Arch.X86_64) -> "${baseLibraryName}_x86_64.dll"
    Pair(OS.WINDOWS, Arch.AARCH64) -> "${baseLibraryName}_aarch64.dll"
    else -> error("The current operating system or CPU architecture is not supported by this mod.")
  }
  private val hashes = mapOf(
    Pair(OS.LINUX, Arch.X86_64) to "af148ab36ccfbaa94c51e255e360e2e642890f2f22dea80188c26a666f862452b23c5f39aa2136ee2c711ed770d3f5901292e2b20b20712e5818aa269d2bff73",
    Pair(OS.LINUX, Arch.AARCH64) to "c46db786a8e0ebc8730033e4cd48a7f055fe35e86d6ef74bb6b2392cec8377b82250315fee615c8cc16ee2ddbc111cdbed4bcce427f67ee934c9456d3d823756",
    Pair(OS.MAC, Arch.X86_64) to "1e1b9f802050ad42eb702ac2b5159c3b47fcc18a9681fccbf389d4f845e627c5e11820aa1710aacec0c4759b64fe5ba86cd7657bb2ede44aad64197038faa749",
    Pair(OS.MAC, Arch.AARCH64) to "a2c9c96f6141cc0d9998f274bb764bb2c46ac932a537f37b9a4354f4aaa7608ce1961b383640ac064ca16ca4e299b814c7bd1d9d0846d347dff59df2f82c092a",
    Pair(OS.WINDOWS, Arch.X86_64) to "8b475b49f8c23c3bc0e7a981ed8c3a1d615faac88d3a8ef224fb11b2c66bfa0f46a329aa9980e29681af079aff74a78c801ddb417d4d656531dc48e99932181c",
    Pair(OS.WINDOWS, Arch.AARCH64) to "9007c000230c496c30de222f1b1425371c24da6d9c5ee850e16f049b41bee4adac8f308e0dbb0b2f91f6f11ac47e0d841a114f93464af95464b6f635106b5a8c",
  )
  private val espeakNgDataName = "espeak-ng-data.zip"
  private val espeakNgDataHash = "ebe661f88682ec37fe18414f2e0b607e97d0b5c711fb4126283f91a70ee7203f6a099a69f8ed152d957f920a466014ba217e803f85f971bdf35ec33be0ed6489"
  fun downloadLibrary() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/$libraryVersion/$libraryName").toURL(), File(libraryName))
    val hash = DigestUtils(MessageDigestAlgorithms.SHA3_512).digestAsHex(File(libraryName))
    val expectedHash = hashes.get(Pair(os, arch))
    if (hash!=expectedHash) {
      FileUtils.delete(File(libraryName))
      error("Downloading $libraryName failed, or your connection is being tampered with: incorrect hash")
    }
    downloadAndExtractEspeakNGData()
  }
  fun downloadAndExtractEspeakNGData() {
    FileUtils.copyURLToFile(URI("https://github.com/emassey0135/audio-navigation-tts/releases/download/$libraryVersion/$espeakNgDataName").toURL(), File(espeakNgDataName))
    val hash = DigestUtils(MessageDigestAlgorithms.SHA3_512).digestAsHex(File(espeakNgDataName))
    if (hash!=espeakNgDataHash) {
      FileUtils.delete(File(espeakNgDataName))
      error("Downloading $espeakNgDataName failed, or your connection is being tampered with: incorrect hash")
    }
    if (Files.exists(Paths.get("espeak-ng-data")))
      FileUtils.deleteDirectory(File("espeak-ng-data"))
    UnzipUtility().unzip(espeakNgDataName, ".")
    FileUtils.delete(File(espeakNgDataName))
  }
  fun initialize() {
    if (!Files.exists(Paths.get(libraryName)))
      downloadLibrary()
    if (!Files.exists(Paths.get("espeak-ng-data")))
      downloadAndExtractEspeakNGData()
  }
}
