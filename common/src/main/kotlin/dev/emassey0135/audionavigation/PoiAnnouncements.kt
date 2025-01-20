package dev.emassey0135.audionavigation

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.Opus
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.PoiType
import dev.emassey0135.audionavigation.Speech

object PoiAnnouncements {
  fun announcePoi(origin: BlockPos, orientation: Vec2f, poi: Poi, distance: Double, detailed: Boolean) {
    val sound = when (poi.type) {
      PoiType.LANDMARK -> "landmark.ogg"
      PoiType.FEATURE -> "feature.ogg"
      PoiType.STRUCTURE -> "structure.ogg"
    }
    Opus.playOpusWithSpeechFromResource("assets/${AudioNavigation.MOD_ID}/sounds/$sound", origin, orientation, poi.pos)
    val text = if (detailed)
      I18n.translate("${AudioNavigation.MOD_ID}.poi_announcement_detailed", poi.name, distance.toInt())
      else
      I18n.translate("${AudioNavigation.MOD_ID}.poi_announcement", poi.name)
    Speech.speak(text, origin, orientation, poi.pos)
  }
  private val poiListQueue = ArrayBlockingQueue<PoiList>(16)
  private var oldPoiList = PoiList(listOf())
  private var mutex = ReentrantLock()
  fun announceNearbyPois(interruptSpeech: Boolean, excludePrevious: Boolean, detailed: Boolean, radius: Double, maxAnnouncements: Int, enableVerticalLimit: Boolean, verticalLimit: Double) {
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player!=null) {
      val origin = BlockPos.ofFloored(player.getPos())
      val orientation = player.getRotationClient()
      AudioNavigationClient.sendPoiRequest(PoiRequestPayload(origin, radius, maxAnnouncements, enableVerticalLimit, verticalLimit))
      thread {
        mutex.lock()
        var poiList = poiListQueue.take()
        if (excludePrevious) {
          val newPoiList = poiList.subtract(oldPoiList)
          oldPoiList = poiList
          poiList = newPoiList
        }
        if (interruptSpeech)
          Speech.interrupt()
        poiList.toList().forEach { poi -> announcePoi(origin, orientation, poi.poi, poi.distance, detailed) }
        mutex.unlock()
      }
    }
  }
  fun receivePoiList(poiList: PoiList) {
    poiListQueue.offer(poiList)
  }
  fun triggerAutomaticAnnouncements() {
    val config = Configs.clientConfig.announcements
    announceNearbyPois(false, true, config.detailedAnnouncements.get(), config.announcementRadius.get().toDouble(), config.maxAnnouncements.get(), config.enableVerticalLimit.get(), config.verticalLimit.get().toDouble())
  }
  fun triggerManualAnnouncements() {
    val config = Configs.clientConfig.manualAnnouncements
    announceNearbyPois(true, false, config.detailedAnnouncements.get(), config.announcementRadius.get().toDouble(), config.maxAnnouncements.get(), config.enableVerticalLimit.get(), config.verticalLimit.get().toDouble())
  }
}
