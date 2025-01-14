package dev.emassey0135.audionavigation

import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigation {
  const val MOD_ID = "audio_navigation"
  @JvmField val logger = LoggerFactory.getLogger(MOD_ID)
  fun respondToPoiRequest(payload: PoiRequestPayload): PoiListPayload {
    val poiList = PoiList.getNearest(payload.pos, payload.radius, payload.maxItems)
    return PoiListPayload(poiList)
  }
  fun initialize() {
    logger.info("Audio Navigation has been initialized.")
  }
}
