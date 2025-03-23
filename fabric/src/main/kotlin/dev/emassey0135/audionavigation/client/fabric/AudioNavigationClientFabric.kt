package dev.emassey0135.audionavigation.client.fabric

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import dev.emassey0135.audionavigation.client.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.PoiListPayload

object AudioNavigationClientFabric: ClientModInitializer {
  override fun onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(PoiListPayload.ID, { payload: PoiListPayload, context: ClientPlayNetworking.Context ->
        AudioNavigationClient.handlePoiList(payload)
    })
    AudioNavigationClient.initialize()
  }
}
