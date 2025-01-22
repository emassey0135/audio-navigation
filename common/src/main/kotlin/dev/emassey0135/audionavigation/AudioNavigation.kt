package dev.emassey0135.audionavigation

import java.util.UUID
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiType

object AudioNavigation {
  const val MOD_ID = "audio_navigation"
  @JvmField val logger = LoggerFactory.getLogger(MOD_ID)
  @JvmStatic @ExpectPlatform fun getWorldUUID(world: ServerWorld): UUID {
    error("This function is not implemented.")
  }
  fun respondToPoiRequest(world: ServerWorld, payload: PoiRequestPayload): PoiListPayload {
    val poiList = if (payload.filterByType) {
      if (payload.enableVerticalLimit)
        PoiList.getNearestWithVerticalLimitAndType(world, payload.pos, payload.radius, payload.maxItems, payload.verticalLimit.get(), payload.type.get())
      else
        PoiList.getNearestWithType(world, payload.pos, payload.radius, payload.maxItems, payload.type.get())
    }
    else {
      if (payload.enableVerticalLimit)
        PoiList.getNearestWithVerticalLimit(world, payload.pos, payload.radius, payload.maxItems, payload.verticalLimit.get())
      else
        PoiList.getNearest(world, payload.pos, payload.radius, payload.maxItems)
    }
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
