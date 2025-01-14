package dev.emassey0135.audionavigation

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.SynchronousQueue
import kotlin.concurrent.thread
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.event.events.client.ClientTickEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Interval
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.Speech

object AudioNavigationClient {
  private val interval = Interval.sec(5)
  private val poiListQueue = SynchronousQueue<PoiList>()
  private var oldPoiList = PoiList(listOf())
  private var mutex = ReentrantLock()
  private fun speakPoi(origin: BlockPos, orientation: Direction, poi: Poi) {
    Speech.speakText(poi.identifier.getPath(), origin, orientation, poi.pos)
  }
  private fun waitForAndSpeakPoiList() {
    mutex.lock()
    val poiList = poiListQueue.take()
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player==null) return
    if (poiList!=oldPoiList) {
      val newPoiList = poiList.subtract(oldPoiList)
      oldPoiList = poiList
      val origin = BlockPos.ofFloored(player.getPos())
      val orientation = player.getFacing()
      newPoiList.toList().forEach { poi -> speakPoi(origin, orientation, poi) }
    }
    mutex.unlock()
  }
  @JvmStatic @ExpectPlatform fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    error("This function is not implemented.")
  }
  fun handlePoiList(payload: PoiListPayload) {
    thread { poiListQueue.put(payload.poiList) }
  }
  fun initialize() {
    val minecraftClient = MinecraftClient.getInstance()
    AudioNavigation.logger.info("The mod has been initialized.")
    interval.beReady()
    ClientTickEvent.CLIENT_LEVEL_PRE.register { world ->
      if (interval.isReady()) {
        val player = minecraftClient.player
        if (player!=null) {
          sendPoiRequest(PoiRequestPayload(BlockPos.ofFloored(player.getPos()), 25.0, 10))
          thread { waitForAndSpeakPoiList() }
        }
      }
    }
  }
}
