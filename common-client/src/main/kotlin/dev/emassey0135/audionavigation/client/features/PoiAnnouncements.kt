package dev.emassey0135.audionavigation.client.features

import java.util.concurrent.locks.ReentrantLock
import java.util.Optional
import java.util.UUID
import kotlin.concurrent.thread
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.client.resources.language.I18n
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient
import dev.emassey0135.audionavigation.client.config.ClientConfig
import dev.emassey0135.audionavigation.client.sound.Opus
import dev.emassey0135.audionavigation.client.speech.Speech
import dev.emassey0135.audionavigation.client.util.Orientation
import dev.emassey0135.audionavigation.client.util.Translation
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiRequest
import dev.emassey0135.audionavigation.poi.PoiType

object PoiAnnouncements {
  fun announcePoi(poi: Poi, distance: Double, playerPosition: BlockPos, playerOrientation: Orientation, announceDistance: Boolean, announceDirection: Boolean, includeVerticalDirection: Boolean, horizontalDirectionType: Orientation.HorizontalDirectionType, verticalDirectionType: Orientation.VerticalDirectionType, announceFromPoiPosition: Boolean) {
    val pos = if (announceFromPoiPosition) poi.pos else playerPosition
    val sound = when (poi.type) {
      PoiType.LANDMARK -> "sense_mobility.ogg"
      PoiType.FEATURE -> "sense_poi.ogg"
      PoiType.STRUCTURE -> "sense_location.ogg"
    }
    Opus.playOpusWithSpeechFromResource("assets/${AudioNavigation.MOD_ID}/audio/$sound", pos)
    val name = when (poi.type) {
      PoiType.LANDMARK -> poi.name
      PoiType.FEATURE -> Translation.translateFeatureName(poi.name)
      PoiType.STRUCTURE -> poi.name
    }
    val distanceString = I18n.get("${AudioNavigation.MOD_ID}.poi_distance", distance.toInt())
    val directionString = Orientation.angleBetween(playerPosition, poi.pos).toSpeakableString(playerOrientation, includeVerticalDirection, horizontalDirectionType, verticalDirectionType)
    val text = when {
      !announceDistance && !announceDirection -> name
      announceDistance && !announceDirection -> "$name, $distanceString"
      !announceDistance && announceDirection -> "$name, $directionString"
      else -> "$name, $distanceString, $directionString"
    }
    Speech.speak(text, pos)
  }
  private var oldPoiList = PoiList()
  private var mutex = ReentrantLock()
  fun announceNearbyPois(interruptSpeech: Boolean, excludePrevious: Boolean, announceDistance: Boolean, announceDirection: Boolean, includeVerticalDirection: Boolean, horizontalDirectionType: Orientation.HorizontalDirectionType, verticalDirectionType: Orientation.VerticalDirectionType, radius: Int, maxAnnouncements: Int, verticalLimit: Optional<Int>, includedFeatures: List<String>) {
    val minecraftClient = Minecraft.getInstance()
    val player = minecraftClient.player
    if (player==null)
      return
    val origin = BlockPos.containing(player.position())
    val orientation = Orientation(player.getRotationVector())
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
      poiList.toList().forEach { poi -> announcePoi(poi.poi, poi.distance, origin, orientation, announceDistance, announceDirection, includeVerticalDirection, horizontalDirectionType, verticalDirectionType, true) }
      mutex.unlock()
    })
    AudioNavigationClient.sendPoiRequest(PoiRequestPayload(requestID, PoiRequest(origin, radius, maxAnnouncements, verticalLimit, Optional.empty(), Optional.of(includedFeatures))))
  }
  fun triggerAutomaticAnnouncements() {
    val config = ClientConfig.instance!!.announcements
    val verticalLimit = if(config.enableVerticalLimit.get()) Optional.of(config.verticalLimit.get().toInt()) else Optional.empty()
    if (config.enableAutomaticAnnouncements.get())
      announceNearbyPois(false, true, config.announceDistance.get(), config.announceDirection.get(), config.includeVerticalDirection.get(), config.horizontalDirectionType.get(), config.verticalDirectionType.get(), config.announcementRadius.get().toInt(), config.maxAnnouncements.get().toInt(), verticalLimit, config.includedFeatures.get())
  }
  fun triggerManualAnnouncements() {
    val config = ClientConfig.instance!!.manualAnnouncements
    val verticalLimit = if(config.enableVerticalLimit.get()) Optional.of(config.verticalLimit.get().toInt()) else Optional.empty()
    announceNearbyPois(true, false, config.announceDistance.get(), config.announceDirection.get(), config.includeVerticalDirection.get(), config.horizontalDirectionType.get(), config.verticalDirectionType.get(), config.announcementRadius.get().toInt(), config.maxAnnouncements.get().toInt(), verticalLimit, config.includedFeatures.get())
  }
}
