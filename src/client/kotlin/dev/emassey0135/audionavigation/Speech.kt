package dev.emassey0135.audionavigation

import java.io.ByteArrayOutputStream
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.unix.LibCAPI.size_t
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.SoundPlayer

interface SynthCallback: Callback {
  fun invoke(wav: Pointer?, numsamples: Int, events: Pointer): Int
}
interface Espeak: Library {
  fun espeak_Initialize(output: Int, buflength: Int, path: String?, options: Int): Int
  fun espeak_SetSynthCallback(synthCallback: SynthCallback)
  fun espeak_Synth(text: String, size: size_t, position: Int, position_type: Int, end_position: Int, flags: Int, unique_identifier: Pointer, user_data: Pointer): Int
  fun espeak_SetParameter(parameter: Int, value: Int, relative: Int): Int
  companion object {
    val INSTANCE: Espeak = Native.load("espeak-ng", Espeak::class.java)
  }
}
class SynthCallbackCollectAudio (val stream: ByteArrayOutputStream): SynthCallback {
  override fun invoke(wav: Pointer?, numsamples: Int, events: Pointer): Int {
    if (wav!=null)
      stream.write(wav.getByteArray(0, 2*numsamples), 0, 2*numsamples)
    return 0
  }
}
object Speech {
  private val espeak = Espeak.INSTANCE
  fun initialize() {
    espeak.espeak_Initialize(2, 0, null, 0)
    espeak.espeak_SetParameter(2, 200, 0)
    AudioNavigation.logger.info("eSpeak initialized.")
  }
  fun speakText(text: String, listenerPos: BlockPos, listenerOrientation: Direction, sourcePos: BlockPos) {
    val callback = SynthCallbackCollectAudio(ByteArrayOutputStream())
    espeak.espeak_SetSynthCallback(callback)
    espeak.espeak_Synth(text, size_t((text.length+1).toLong()), 0, 1, 0, 0, Pointer(0), Pointer(0))
    SoundPlayer.playSound(callback.stream.toByteArray(), listenerPos, listenerOrientation, sourcePos)
  }
}
