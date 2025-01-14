package dev.emassey0135.audionavigation.fabric

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationFabric: ModInitializer {
  override fun onInitialize() {
    PayloadTypeRegistry.playC2S().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.playS2C().register(PoiListPayload.ID, PoiListPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        context.responseSender().sendPacket(AudioNavigation.respondToPoiRequest(payload))
      })
    AudioNavigation.initialize()
  }
}
