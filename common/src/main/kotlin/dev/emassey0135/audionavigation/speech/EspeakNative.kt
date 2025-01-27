package dev.emassey0135.audionavigation.speech

import dev.architectury.platform.Platform
import dev.emassey0135.audionavigation.AudioNavigation

class EspeakNative {
  external fun initialize(path: String)
  external fun speak(text: String): ByteArray
  external fun setRate(rate: Int)
  external fun setVolume(volume: Int)
  external fun setPitch(pitch: Int)
  external fun setPitchRange(pitchRange: Int)
  external fun listVoices(language: String): Array<String>
  external fun setVoice(name: String)
  companion object {
    init {
      System.load(Platform.getGameFolder().resolve("libaudio_navigation_tts.so").toString())
    }
    val INSTANCE = EspeakNative()
  }
}
