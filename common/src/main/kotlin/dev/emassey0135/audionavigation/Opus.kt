package dev.emassey0135.audionavigation

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.file.Files
import java.util.LinkedList
import org.lwjgl.BufferUtils
import org.lwjgl.openal.EXTFloat32
import org.lwjgl.util.opus.OpusFile
import dev.architectury.platform.Platform
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.SoundPlayer

object Opus {
  fun playOpus(source: String, data: ByteBuffer) {
    val opusFile = OpusFile.op_open_memory(data, null)
    if (opusFile==0L)
      error("Invalid OPUS data")
    val channels = OpusFile.op_channel_count(opusFile, 0)
    if (channels<1)
      error("Invalid channel count")
    val arrays = LinkedList<FloatArray>()
    var size = 0
    val pcm = BufferUtils.createFloatBuffer(1024)
    var pcmArray: FloatArray
    var samples: Int
    do {
      if (channels<=2)
        samples = OpusFile.op_read_float(opusFile, pcm, null)
      else
        samples = OpusFile.op_read_float_stereo(opusFile, pcm)
      if (samples!=0) {
        pcmArray = FloatArray(samples*(if (channels>2) 2 else channels))
        pcm.get(pcmArray, 0, samples*(if (channels>2) 2 else channels))
        arrays.add(pcmArray)
        size += samples
        pcm.clear()
      }
    }
    while (samples!=0)
    val result = BufferUtils.createFloatBuffer(size)
    arrays.forEach { array -> result.put(array) }
    result.flip()
    SoundPlayer.play(source, (if (channels==1) EXTFloat32.AL_FORMAT_MONO_FLOAT32 else EXTFloat32.AL_FORMAT_STEREO_FLOAT32), 48000, result)
    OpusFile.op_free(opusFile)
  }
  fun playOpusFromResource(source: String, resourcePath: String) {
    val path = Platform.getMod(AudioNavigation.MOD_ID).findResource(resourcePath)
    if (!path.isPresent())
      error("Invalid resource path: ${resourcePath}")
    val data = Files.readAllBytes(path.get())
    val buffer = BufferUtils.createByteBuffer(data.size)
    buffer.put(data)
    buffer.flip()
    playOpus(source, buffer)
  }
}