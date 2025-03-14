package dev.emassey0135.audionavigation.features

import java.util.concurrent.locks.ReentrantLock
import java.util.Optional
import java.util.UUID
import kotlin.concurrent.thread
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.config.ClientConfig
import dev.emassey0135.audionavigation.poi.Features
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiRequest
import dev.emassey0135.audionavigation.poi.PoiType
import dev.emassey0135.audionavigation.sound.Opus
import dev.emassey0135.audionavigation.speech.Speech

object PoiAnnouncements {
  fun announcePoi(poi: Poi, distance: Double, detailed: Boolean) {
    val sound = when (poi.type) {
      PoiType.LANDMARK -> "sense_mobility.ogg"
      PoiType.FEATURE -> "sense_poi.ogg"
      PoiType.STRUCTURE -> "sense_location.ogg"
    }
    Opus.playOpusWithSpeechFromResource("assets/${AudioNavigation.MOD_ID}/audio/$sound", poi.pos)
    val name = when (poi.type) {
      PoiType.LANDMARK -> poi.name
      PoiType.FEATURE -> Features.translateName(poi.name)
      PoiType.STRUCTURE -> poi.name
    }
    val text = if (detailed)
      "$name, ${I18n.translate("${AudioNavigation.MOD_ID}.poi_distance", distance.toInt())}"
      else
      name
    Speech.speak(text, poi.pos)
  }
  private var oldPoiList = PoiList()
  private var mutex = ReentrantLock()
  fun announceNearbyPois(interruptSpeech: Boolean, excludePrevious: Boolean, detailed: Boolean, radius: Int, maxAnnouncements: Int, verticalLimit: Optional<Int>, includedFeatures: List<String>) {
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player==null)
      return
    val origin = BlockPos.ofFloored(player.getPos())
    val requestID = UUID.randomUUID()
    AudioNavigationClient.registerPoiListHandler(requestID, { payload ->
      mutex.lock()
      var poiList = payload.poiList
      if (excludePrevious) {
        val newPoiList = poiList.subtract(oldPoiList)
        oldPoiList = poiList
        poiList = newPoiList
      }
      if (interruptSpeech)
        Speech.interrupt()
      poiList.toList().forEach { poi -> announcePoi(poi.poi, poi.distance, detailed) }
      mutex.unlock()
    })
    AudioNavigationClient.sendPoiRequest(PoiRequestPayload(requestID, PoiRequest(origin, radius, maxAnnouncements, verticalLimit, Optional.empty(), Optional.of(includedFeatures))))
  }
  fun triggerAutomaticAnnouncements() {
    val config = ClientConfig.instance!!.announcements
    val verticalLimit = if(config.enableVerticalLimit.get()) Optional.of(config.verticalLimit.get().toInt()) else Optional.empty()
    announceNearbyPois(false, true, config.detailedAnnouncements.get(), config.announcementRadius.get().toInt(), config.maxAnnouncements.get().toInt(), verticalLimit, config.includedFeatures.get())
  }
  fun triggerManualAnnouncements() {
    val config = ClientConfig.instance!!.manualAnnouncements
    val verticalLimit = if(config.enableVerticalLimit.get()) Optional.of(config.verticalLimit.get().toInt()) else Optional.empty()
    announceNearbyPois(true, false, config.detailedAnnouncements.get(), config.announcementRadius.get().toInt(), config.maxAnnouncements.get().toInt(), verticalLimit, config.includedFeatures.get())
  }
}
