package dev.emassey0135.audionavigation

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.AL11
import org.lwjgl.openal.ALC11
import org.lwjgl.openal.EXTThreadLocalContext
import org.lwjgl.openal.SOFTHRTF
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Orientation

object SoundPlayer {
  private val sources: HashMap<String, Int> = HashMap()
  private val tasks = ArrayBlockingQueue<() -> Unit>(16)
  var isInitialized = false
  fun initialize() {
    thread {
      val alDevice = ALC11.nalcOpenDevice(0L)
      if (alDevice==0L)
        error("OpenAL initialization failed: could not open device")
      val alContext = ALC11.alcCreateContext(alDevice!!, intArrayOf(SOFTHRTF.ALC_HRTF_SOFT, ALC11.ALC_TRUE, 0))
      if (alContext==0L)
        error("OpenAL initialization failed: could not create context")
      EXTThreadLocalContext.alcSetThreadContext(alContext!!)
      val alcCapabilities = ALC.createCapabilities(alDevice)
      val alCapabilities = AL.createCapabilities(alcCapabilities)
      isInitialized = true
      AudioNavigation.logger.info("OpenAL initialized.")
      var task: () -> Unit
      while (true) {
        task = tasks.take()
        task()
      }
    }
    while (!isInitialized) {
      Thread.sleep(10)
    }
  }
  fun addSource(name: String) {
    thread {
      tasks.put {
        sources.put(name, AL11.alGenSources())
      }
    }
  }
  fun setSourceMaxDistance(name: String, maxDistance: Float) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        AL11.alSourcef(sources.get(name)!!, AL11.AL_MAX_DISTANCE, maxDistance)
      }
    }
  }
  fun setSourceRolloffFactor(name: String, rolloffFactor: Float) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        AL11.alSourcef(sources.get(name)!!, AL11.AL_ROLLOFF_FACTOR, rolloffFactor)
      }
    }
  }
  fun setSourcePosition(name: String, pos: BlockPos) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        AL11.alSource3f(sources.get(name)!!, AL11.AL_POSITION, pos.getX().toFloat(), pos.getY().toFloat(), pos.getZ().toFloat())
      }
    }
  }
  fun updateListenerPosition() {
    thread {
      tasks.put(fun(): Unit {
        val minecraftClient = MinecraftClient.getInstance()
        val player = minecraftClient.player
        if (player==null)
          return
        val pos = BlockPos.ofFloored(player.getPos())
        val orientation = Orientation(player.getRotationClient())
        AL11.alListener3f(AL11.AL_POSITION, pos.getX().toFloat(), pos.getY().toFloat(), pos.getZ().toFloat())
        val vector = orientation.toVector()
        AL11.alListenerfv(AL11.AL_ORIENTATION, floatArrayOf(vector.x.toFloat(), vector.y.toFloat(), vector.z.toFloat(), 0f, 1f, 0f))
      })
    }
  }
  fun play(name: String, format: Int, sampleRate: Int, data: ByteBuffer) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        val buffer = AL11.alGenBuffers()
        AL11.alBufferData(buffer, format, data, sampleRate)
        AL11.alSourcei(sources.get(name)!!, AL11.AL_BUFFER, buffer)
        AL11.alSourcePlay(sources.get(name)!!)
      }
    }
  }
  fun play(name: String, format: Int, sampleRate: Int, data: ShortBuffer) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        val buffer = AL11.alGenBuffers()
        AL11.alBufferData(buffer, format, data, sampleRate)
        AL11.alSourcei(sources.get(name)!!, AL11.AL_BUFFER, buffer)
        AL11.alSourcePlay(sources.get(name)!!)
      }
    }
  }
  fun play(name: String, format: Int, sampleRate: Int, data: FloatBuffer) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        val buffer = AL11.alGenBuffers()
        AL11.alBufferData(buffer, format, data, sampleRate)
        AL11.alSourcei(sources.get(name)!!, AL11.AL_BUFFER, buffer)
        AL11.alSourcePlay(sources.get(name)!!)
      }
    }
  }
  fun stop(name: String) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        AL11.alSourceStop(sources.get(name)!!)
      }
    }
  }
  fun getSourceState(name: String, callback: (Int) -> Unit) {
    thread {
      tasks.put {
        if (!sources.containsKey(name))
          error("Source has not been added: ${name}")
        callback(AL11.alGetSourcei(sources.get(name)!!, AL11.AL_SOURCE_STATE))
      }
    }
  }
}
