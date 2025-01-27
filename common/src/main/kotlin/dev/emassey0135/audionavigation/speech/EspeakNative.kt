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
      val os = System.getProperty("os.name")
      val file_name = when {
        os.startsWith("Windows") -> "audio_navigation_tts.dll"
        os.startsWith("Linux") -> "libaudio_navigation_tts.so"
        else -> "libaudio_navigation_tts.so"
      }
      System.load(Platform.getGameFolder().resolve(file_name).toString())
    }
    val INSTANCE = EspeakNative()
  }
}
