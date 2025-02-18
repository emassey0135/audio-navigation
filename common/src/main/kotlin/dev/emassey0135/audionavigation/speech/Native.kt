package dev.emassey0135.audionavigation.speech

import java.nio.file.Paths
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Library
import dev.emassey0135.audionavigation.speech.Voice

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
