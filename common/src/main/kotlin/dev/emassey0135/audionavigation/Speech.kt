package dev.emassey0135.audionavigation

import java.lang.Thread
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL11
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.ClientConfig
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.speech.Native
import dev.emassey0135.audionavigation.speech.Voice

private data class SpeechRequest(val speakRequest: SpeakRequest?, val playSoundRequest: PlaySoundRequest?, val sourcePos: BlockPos) {
  data class SpeakRequest(val text: String)
  data class PlaySoundRequest(val format: Int, val sampleRate: Int, val byteBuffer: ByteBuffer?, val shortBuffer: ShortBuffer?, val floatBuffer: FloatBuffer?)
}
object Speech {
  private var voices: List<Voice> = listOf()
  private val speechRequests = ArrayBlockingQueue<SpeechRequest>(64)
  var isInitialized = false
  fun initialize() {
    Native.INSTANCE.initialize()
    SoundPlayer.addSource("speech")
    voices = Native.INSTANCE.listVoices().toList()
    isInitialized = true
    AudioNavigation.logger.info("Speech initialized.")
    thread {
      var speechRequest: SpeechRequest
      var isPlaying = false
      while (true) {
        speechRequest = speechRequests.take()
        SoundPlayer.updateListenerPosition()
        SoundPlayer.setSourcePosition("speech", speechRequest.sourcePos)
        if (speechRequest.speakRequest!=null) {
          val config = ClientConfig.instance!!.speech
          val voice = config.voice.get()
          val result = Native.INSTANCE.speak(voice.synthesizer, voice.name, voice.language, config.rate.get(), config.volume.get(), config.pitch.get(), speechRequest.speakRequest.text)
          val buffer = BufferUtils.createByteBuffer(result.pcm.size)
          buffer.put(result.pcm)
          buffer.flip()
          SoundPlayer.play("speech", AL11.AL_FORMAT_MONO16, result.sampleRate, buffer)
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
  fun synthesizers(): List<String> {
    return voices.map { it.synthesizer }.toSet().toList().sorted()
  }
  fun languages(): List<String> {
    return voices.map { it.language }.toSet().toList().sorted()
  }
  fun filterVoices(synthesizers: List<String>, languages: List<String>): List<Voice> {
    return voices.filter { it.synthesizer in synthesizers && it.language in languages }.sortedBy { it.displayName.lowercase() }
  }
  fun configure() {
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
