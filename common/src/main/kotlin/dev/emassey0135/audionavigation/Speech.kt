package dev.emassey0135.audionavigation

import java.lang.Thread
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import org.lwjgl.openal.AL11
import net.minecraft.util.math.BlockPos
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.ClientConfig
import dev.emassey0135.audionavigation.EspeakVoice
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.speech.EspeakNative

private interface Espeak: Library {
  fun espeak_ListVoices(voice_spec: EspeakVoice?): Pointer
  companion object {
    val INSTANCE: Espeak = Native.load("espeak-ng", Espeak::class.java)
  }
}
private data class SpeechRequest(val speakRequest: SpeakRequest?, val playSoundRequest: PlaySoundRequest?, val sourcePos: BlockPos) {
  data class SpeakRequest(val text: String)
  data class PlaySoundRequest(val format: Int, val sampleRate: Int, val byteBuffer: ByteBuffer?, val shortBuffer: ShortBuffer?, val floatBuffer: FloatBuffer?)
}
object Speech {
  private val espeak = Espeak.INSTANCE
  fun setRate(rate: Int) {
    EspeakNative.INSTANCE.setRate(rate)
  }
  fun setVolume(volume: Int) {
    EspeakNative.INSTANCE.setVolume(volume)
  }
  fun setPitch(pitch: Int) {
    EspeakNative.INSTANCE.setPitch(pitch)
  }
  fun setPitchRange(pitchRange: Int) {
    EspeakNative.INSTANCE.setPitchRange(pitchRange)
  }
  fun listVoices(language: String): List<String> {
    val voiceSpec = EspeakVoice()
    voiceSpec.name = null
    voiceSpec.languages = language.replace('_', '-')
    voiceSpec.identifier = null
    voiceSpec.gender = 0
    voiceSpec.age = 0
    voiceSpec.variant = 0
    val voices = espeak.espeak_ListVoices(voiceSpec)
    if (Pointer.nativeValue(voices)==0L)
      return listOf()
    val result = mutableListOf<String>()
    var offset = 0L
    var voice: EspeakVoice
    while (Pointer.nativeValue(voices.getPointer(offset))!=0L) {
      voice = EspeakVoice(voices.getPointer(offset))
      result.add(voice.name!!)
      offset += 8L
    }
    return result.toList()
  }
  fun setVoice(name: String) {
    EspeakNative.INSTANCE.setVoice(name)
  }
  private val speechRequests = ArrayBlockingQueue<SpeechRequest>(64)
  var isInitialized = false
  fun initialize() {
    EspeakNative.INSTANCE.initialize()
    SoundPlayer.addSource("speech")
    isInitialized = true
    AudioNavigation.logger.info("eSpeak initialized.")
    thread {
      var speechRequest: SpeechRequest
      var isPlaying = false
      while (true) {
        speechRequest = speechRequests.take()
        SoundPlayer.updateListenerPosition()
        SoundPlayer.setSourcePosition("speech", speechRequest.sourcePos)
        if (speechRequest.speakRequest!=null) {
          val buffer = EspeakNative.INSTANCE.speak(speechRequest.speakRequest.text)
          SoundPlayer.play("speech", AL11.AL_FORMAT_MONO16, 22050, buffer)
        }
        else if (speechRequest.playSoundRequest!=null) {
          when {
            speechRequest.playSoundRequest.byteBuffer!=null -> SoundPlayer.play("speech", speechRequest.playSoundRequest.format, speechRequest.playSoundRequest.sampleRate, speechRequest.playSoundRequest.byteBuffer)
            speechRequest.playSoundRequest.shortBuffer!=null -> SoundPlayer.play("speech", speechRequest.playSoundRequest.format, speechRequest.playSoundRequest.sampleRate, speechRequest.playSoundRequest.shortBuffer)
            speechRequest.playSoundRequest.floatBuffer!=null -> SoundPlayer.play("speech", speechRequest.playSoundRequest.format, speechRequest.playSoundRequest.sampleRate, speechRequest.playSoundRequest.floatBuffer)
            else -> error("No sound data in play sound request")
          }
        }
        else {
          error("Empty speech request")
        }
      while (!isPlaying) {
        SoundPlayer.getSourceState("speech", { state -> isPlaying = state==AL11.AL_PLAYING })
        Thread.sleep(10)
      }
      while (isPlaying) {
        SoundPlayer.getSourceState("speech", { state -> isPlaying = state==AL11.AL_PLAYING })
        Thread.sleep(10)
      }
      }
    }
  }
  fun configure() {
    setVoice(ClientConfig.instance!!.speech.voice.get())
    setRate(ClientConfig.instance!!.speech.rate.get())
    setVolume(ClientConfig.instance!!.speech.volume.get())
    setPitch(ClientConfig.instance!!.speech.pitch.get())
    setPitchRange(ClientConfig.instance!!.speech.pitchRange.get())
    SoundPlayer.setSourceMaxDistance("speech", ClientConfig.instance!!.sound.maxDistance.get().toFloat())
    SoundPlayer.setSourceRolloffFactor("speech", ClientConfig.instance!!.sound.rolloffFactor.get())
  }
  fun interrupt() {
    speechRequests.clear()
    SoundPlayer.stop("speech")
  }
  fun speak(text: String, sourcePos: BlockPos) {
    speechRequests.offer(SpeechRequest(SpeechRequest.SpeakRequest(text), null, sourcePos))
  }
  fun playSound(format: Int, sampleRate: Int, data: ByteBuffer, sourcePos: BlockPos) {
    speechRequests.offer(SpeechRequest(null, SpeechRequest.PlaySoundRequest(format, sampleRate, data, null, null), sourcePos))
  }
  fun playSound(format: Int, sampleRate: Int, data: ShortBuffer, sourcePos: BlockPos) {
    speechRequests.offer(SpeechRequest(null, SpeechRequest.PlaySoundRequest(format, sampleRate, null, data, null), sourcePos))
  }
  fun playSound(format: Int, sampleRate: Int, data: FloatBuffer, sourcePos: BlockPos) {
    speechRequests.offer(SpeechRequest(null, SpeechRequest.PlaySoundRequest(format, sampleRate, null, null, data), sourcePos))
  }
}
