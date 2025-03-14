package dev.emassey0135.audionavigation

import java.util.UUID
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiRequest
import dev.emassey0135.audionavigation.poi.PoiType
import dev.emassey0135.audionavigation.util.Database

object AudioNavigation {
  const val MOD_ID = "audio_navigation"
  @JvmField val logger = LoggerFactory.getLogger(MOD_ID)
  @JvmStatic @ExpectPlatform fun getWorldUUID(world: ServerWorld): UUID {
    error("This function is not implemented.")
  }
  fun respondToPoiRequest(world: ServerWorld, payload: PoiRequestPayload): PoiListPayload {
    val poiList = PoiList.getNearest(world, payload.poiRequest)
    return PoiListPayload(payload.requestID, poiList)
  }
  fun addLandmark(world: ServerWorld, name: String, pos: BlockPos) {
    Poi(PoiType.LANDMARK, name, pos).addToDatabase(world)
  }
  fun deleteLandmark(id: Int) {
    Poi.deleteLandmark(id)
  }
  fun initialize() {
    Database.initialize()
    logger.info("Audio Navigation common has been initialized.")
  }
}
