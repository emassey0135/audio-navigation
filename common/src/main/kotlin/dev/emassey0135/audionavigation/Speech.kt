package dev.emassey0135.audionavigation

import java.io.ByteArrayOutputStream
import java.lang.Thread
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL11
import net.minecraft.util.math.BlockPos
import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.unix.LibCAPI.size_t
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.SoundPlayer

private interface SynthCallback: Callback {
  fun invoke(wav: Pointer?, numsamples: Int, events: Pointer): Int
}
private interface Espeak: Library {
  fun espeak_Initialize(output: Int, buflength: Int, path: String?, options: Int): Int
  fun espeak_SetSynthCallback(synthCallback: SynthCallback)
  fun espeak_Synth(text: String, size: size_t, position: Int, position_type: Int, end_position: Int, flags: Int, unique_identifier: Pointer, user_data: Pointer): Int
  fun espeak_SetParameter(parameter: Int, value: Int, relative: Int): Int
  companion object {
    val INSTANCE: Espeak = Native.load("espeak-ng", Espeak::class.java)
  }
}
private class SynthCallbackCollectAudio (val stream: ByteArrayOutputStream): SynthCallback {
  override fun invoke(wav: Pointer?, numsamples: Int, events: Pointer): Int {
    if (wav!=null)
      stream.write(wav.getByteArray(0, 2*numsamples), 0, 2*numsamples)
    return 0
  }
}
private data class SpeechRequest(val speakRequest: SpeakRequest?, val playSoundRequest: PlaySoundRequest?, val sourcePos: BlockPos) {
  data class SpeakRequest(val text: String)
  data class PlaySoundRequest(val format: Int, val sampleRate: Int, val byteBuffer: ByteBuffer?, val shortBuffer: ShortBuffer?, val floatBuffer: FloatBuffer?)
}
object Speech {
  private val espeak = Espeak.INSTANCE
  fun setRate(rate: Int) {
    espeak.espeak_SetParameter(1, rate, 0)
  }
  fun setVolume(volume: Int) {
    espeak.espeak_SetParameter(2, volume, 0)
  }
  fun setPitch(pitch: Int) {
    espeak.espeak_SetParameter(3, pitch, 0)
  }
  fun setPitchRange(pitchRange: Int) {
    espeak.espeak_SetParameter(4, pitchRange, 0)
  }
  private val speechRequests = ArrayBlockingQueue<SpeechRequest>(64)
  var isInitialized = false
  fun initialize() {
    espeak.espeak_Initialize(2, 0, null, 0)
    setRate(Configs.clientConfig.speech.rate.get())
    setVolume(Configs.clientConfig.speech.volume.get())
    setPitch(Configs.clientConfig.speech.pitch.get())
    setPitchRange(Configs.clientConfig.speech.pitchRange.get())
    SoundPlayer.addSource("speech")
    SoundPlayer.setSourceMaxDistance("speech", Configs.clientConfig.sound.maxDistance.get().toFloat())
    SoundPlayer.setSourceRolloffFactor("speech", Configs.clientConfig.sound.rolloffFactor.get())
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
          val callback = SynthCallbackCollectAudio(ByteArrayOutputStream())
          espeak.espeak_SetSynthCallback(callback)
          espeak.espeak_Synth(speechRequest.speakRequest.text, size_t((speechRequest.speakRequest.text.length+1).toLong()), 0, 1, 0, 0, Pointer(0), Pointer(0))
          val array = callback.stream.toByteArray()
          val buffer = BufferUtils.createByteBuffer(array.size)
          buffer.put(array)
          buffer.flip()
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
        isPlaying = true
        while (isPlaying) {
          Thread.sleep(10)
          SoundPlayer.getSourceState("speech", { state -> isPlaying = state==AL11.AL_PLAYING })
        }
      }
    }
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
