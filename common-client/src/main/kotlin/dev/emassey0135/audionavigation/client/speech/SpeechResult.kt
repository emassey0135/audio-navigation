package dev.emassey0135.audionavigation.client.speech

class SpeechResult(val pcm: ByteArray, sampleFormatByte: Byte, val sampleRate: Int) {
  val sampleFormat = SampleFormat.entries.get(sampleFormatByte.toInt())
  enum class SampleFormat {
    S16,
    F32
  }
}
