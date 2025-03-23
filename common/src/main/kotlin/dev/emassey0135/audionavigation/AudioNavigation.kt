package dev.emassey0135.audionavigation

import java.util.UUID
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.config.ServerConfiguration
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiRequest
import dev.emassey0135.audionavigation.poi.PoiType
import dev.emassey0135.audionavigation.util.Database

interface AudioNavigationPlatform {
  fun getWorldUUID(world: ServerLevel): UUID
}
object AudioNavigation {
  const val MOD_ID = "audio_navigation"
  @JvmField val logger = LoggerFactory.getLogger(MOD_ID)
  var platform: AudioNavigationPlatform? = null
  var config: ServerConfiguration? = null
  fun respondToPoiRequest(world: ServerLevel, payload: PoiRequestPayload): PoiListPayload {
    val poiList = PoiList.getNearest(world, payload.poiRequest)
    return PoiListPayload(payload.requestID, poiList)
  }
  fun addLandmark(world: ServerLevel, name: String, pos: BlockPos) {
    Poi(PoiType.LANDMARK, name, pos).addToDatabase(world)
  }
  fun deleteLandmark(id: Int) {
    Poi.deleteLandmark(id)
  }
  fun initialize(audioNavigationPlatform: AudioNavigationPlatform, serverConfig: ServerConfiguration) {
    platform = audioNavigationPlatform
    config = serverConfig
    Database.initialize()
    logger.info("Audio Navigation common has been initialized.")
  }
}
