package dev.emassey0135.audionavigation.speech

import java.nio.file.Paths
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Library

data class Voice(val synthesizer: String, val displayName: String, val name: String, val language: String)
class Native {
  external fun initialize()
  external fun speak(voice: String, rate: Int, volume: Byte, pitch: Byte, pitch_range: Byte, text: String): ByteArray
  external fun listVoices(): Array<Voice>
  companion object {
    init {
      System.load(Paths.get(Library.libraryName).toAbsolutePath().toString())
    }
    val INSTANCE = Native()
  }
}
