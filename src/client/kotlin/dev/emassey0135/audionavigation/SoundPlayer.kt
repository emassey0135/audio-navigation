package dev.emassey0135.audionavigation

import java.lang.Thread
import kotlin.concurrent.thread
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.AL11
import org.lwjgl.openal.ALC11
import org.lwjgl.openal.EXTThreadLocalContext
import org.lwjgl.openal.SOFTHRTF
import dev.emassey0135.audionavigation.AudioNavigation

object SoundPlayer {
  private var alDevice: Long? = null
  private var alContext: Long? = null
  fun initialize() {
    alDevice = ALC11.nalcOpenDevice(0L)
    if (alDevice==null || alDevice==0L) {
      AudioNavigation.logger.error("OpenAL initialization failed: could not open device")
      return
    }
    alContext = ALC11.alcCreateContext(alDevice!!, intArrayOf(SOFTHRTF.ALC_HRTF_SOFT, ALC11.ALC_TRUE, 0))
    if (alContext==null || alContext==0L) {
      AudioNavigation.logger.error("OpenAL initialization failed: could not create context")
      return
    }
    AudioNavigation.logger.info("OpenAL initialized.")
  }
  fun playSound(bytes: ByteArray, listenerPos: BlockPos, listenerOrientation: Direction, sourcePos: BlockPos) {
    val process = thread {
      EXTThreadLocalContext.alcSetThreadContext(alContext!!)
      val alcCapabilities = ALC.createCapabilities(alDevice!!)
      val alCapabilities = AL.createCapabilities(alcCapabilities)
      val buffer = AL11.alGenBuffers()
      val byteBuffer = BufferUtils.createByteBuffer(bytes.size)
      byteBuffer.put(bytes)
      byteBuffer.flip()
      AL11.alBufferData(buffer, AL11.AL_FORMAT_MONO16, byteBuffer, 22050)
      val source = AL11.alGenSources()
      AL11.alSourcef(source, AL11.AL_MAX_DISTANCE, 100f)
      AL11.alSourcef(source, AL11.AL_ROLLOFF_FACTOR, 0.1f)
      AL11.alListener3f(AL11.AL_POSITION, listenerPos.getX().toFloat(), listenerPos.getY().toFloat(), listenerPos.getZ().toFloat())
      val vector = listenerOrientation.getUnitVector()
      AL11.alListenerfv(AL11.AL_ORIENTATION, floatArrayOf(vector.x, vector.y, vector.z, 0f, 1f, 0f))
      AL11.alSource3f(source, AL11.AL_POSITION, sourcePos.getX().toFloat(), sourcePos.getY().toFloat(), sourcePos.getZ().toFloat())
      AL11.alSourcei(source, AL11.AL_BUFFER, buffer)
      AL11.alSourcePlay(source)
      while (AL11.alGetSourcei(source, AL11.AL_SOURCE_STATE) == AL11.AL_PLAYING) {
        Thread.sleep(10)
      }
    }
    process.join()
  }
}
