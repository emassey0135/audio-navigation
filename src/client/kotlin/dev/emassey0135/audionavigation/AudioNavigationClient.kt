package dev.emassey0135.audionavigation

import java.lang.Thread
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.SynchronousQueue
import java.util.LinkedList
import kotlin.concurrent.thread
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Interval
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.Speech

object AudioNavigationClient : ClientModInitializer {
  private val interval = Interval.sec(5)
  private val poiListQueue = SynchronousQueue<PoiList>()
  private var oldPoiList = PoiList(listOf())
  private var mutex = ReentrantLock()
  private fun speakPoi(origin: BlockPos, poi: Poi) {
    Speech.speakText(poi.identifier.getPath(), origin, poi.pos)
  }
  private fun waitForAndSpeakPoiList() {
    mutex.lock()
    val poiList = poiListQueue.take()
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player==null) return
    AudioNavigation.logger.info("a: " + poiList.toSet().toString())
    val filteredPoiList = poiList.filterByDistance(BlockPos.ofFloored(player.getPos()), 25f)
    AudioNavigation.logger.info("b: " + filteredPoiList.toSet().toString())
    AudioNavigation.logger.info("c: " + oldPoiList.toSet().toString())
    if (filteredPoiList!=oldPoiList) {
      val newPoiList = filteredPoiList.subtract(oldPoiList)
      AudioNavigation.logger.info("d: " + newPoiList.toSet().toString())
      oldPoiList = filteredPoiList
      val sortedPoiList = newPoiList.sortByDistance(BlockPos.ofFloored(player.getPos()))
      AudioNavigation.logger.info("e: " + sortedPoiList.toString())
      sortedPoiList.forEach { poi -> speakPoi(BlockPos.ofFloored(player.getPos()), poi) }
    }
    mutex.unlock()
  }
  override fun onInitializeClient() {
    val minecraftClient = MinecraftClient.getInstance()
    SoundPlayer.initialize()
    Speech.initialize()
    ClientPlayNetworking.registerGlobalReceiver(PoiListPayload.ID, { payload: PoiListPayload, context: ClientPlayNetworking.Context ->
        thread { poiListQueue.put(payload.poiList) }
    })
    AudioNavigation.logger.info("The mod has been initialized.", 0f, 0f, 0f)
    interval.beReady()
    ClientTickEvents.START_WORLD_TICK.register { world ->
      if (interval.isReady()) {
        if (ClientPlayNetworking.canSend(PoiRequestPayload.ID)) {
          val player = minecraftClient.player
          if (player!=null) {
            ClientPlayNetworking.send(PoiRequestPayload(BlockPos.ofFloored(player.getPos())))
            thread { waitForAndSpeakPoiList() }
          }
        }
      }
    }
  }
}