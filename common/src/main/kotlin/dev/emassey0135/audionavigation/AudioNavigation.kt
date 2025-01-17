package dev.emassey0135.audionavigation

import java.util.UUID
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.Configs
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
    val poiList = if (payload.enableVerticalLimit)
      PoiList.getNearestWithVerticalLimit(world, payload.pos, payload.radius, payload.maxItems, payload.verticalLimit)
      else
      PoiList.getNearest(world, payload.pos, payload.radius, payload.maxItems)
    return PoiListPayload(poiList)
  }
  fun addLandmark(world: ServerWorld, name: String, pos: BlockPos) {
    Poi(PoiType.LANDMARK, Identifier.of("landmark", name), pos).addToDatabase(world)
  }
  fun initialize() {
    Configs.initialize()
    Database.initialize()
    logger.info("Audio Navigation has been initialized.")
  }
}
