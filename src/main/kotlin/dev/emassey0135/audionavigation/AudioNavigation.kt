package dev.emassey0135.audionavigation

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.PoiList

object AudioNavigation : ModInitializer {
  @JvmField val logger = LoggerFactory.getLogger("audio-navigation")
  override fun onInitialize() {
    PayloadTypeRegistry.playC2S().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.playS2C().register(PoiListPayload.ID, PoiListPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        val poiList = PoiList.getNearest(payload.pos, payload.radius)
        if (poiList!=null)
          context.responseSender().sendPacket(PoiListPayload(poiList))
      })
  }
}
