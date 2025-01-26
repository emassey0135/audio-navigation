package dev.emassey0135.audionavigation.speech

import java.nio.ByteBuffer

class EspeakNative {
  external fun initialize()
  external fun speak(text: String): ByteBuffer
  external fun setRate(rate: Int)
  external fun setVolume(volume: Int)
  external fun setPitch(pitch: Int)
  external fun setPitchRange(pitchRange: Int)
  external fun listVoices(language: String): Array<String>
  external fun setVoice(name: String)
  companion object {
    init {
      System.load("/home/elijah/.minecraft/libaudio_navigation_tts.so")
    }
    val INSTANCE = EspeakNative()
  }
}
