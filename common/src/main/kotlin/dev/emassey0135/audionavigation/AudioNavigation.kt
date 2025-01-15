package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.networking.api.ClientPlayNetworkContext
import me.fzzyhmstrs.fzzy_config.networking.api.ServerPlayNetworkContext
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.packets.PacketIdentifiers
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
    Configs.initialize()
    Database.initialize()
    ConfigApi.network().registerC2S(PoiRequestPayload.ID, PoiRequestPayload.CODEC, { payload: PoiRequestPayload, context: ServerPlayNetworkContext ->
        if (context.canReply(PacketIdentifiers.POI_LIST_ID))
          context.reply(AudioNavigation.respondToPoiRequest(payload))
      })
    ConfigApi.network().registerS2C(PoiListPayload.ID, PoiListPayload.CODEC, { payload: PoiListPayload, context: ClientPlayNetworkContext ->
        AudioNavigationClient.handlePoiList(payload)
    })
    logger.info("Audio Navigation has been initialized.")
  }
}
