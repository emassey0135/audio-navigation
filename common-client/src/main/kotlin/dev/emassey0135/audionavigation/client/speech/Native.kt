package dev.emassey0135.audionavigation.client.speech

import java.nio.file.Paths
import dev.emassey0135.audionavigation.client.util.Library

class Native {
  external fun initialize()
  external fun speak(synthesizer: String, voice: String, language: String, rate: Byte, volume: Byte, pitch: Byte, text: String): SpeechResult
  external fun listVoices(): Array<Voice>
  companion object {
    init {
      System.load(Paths.get(Library.libraryName).toAbsolutePath().toString())
    }
    val INSTANCE = Native()
  }
}
