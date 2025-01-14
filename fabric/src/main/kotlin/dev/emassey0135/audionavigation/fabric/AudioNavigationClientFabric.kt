package dev.emassey0135.audionavigation.fabric

import kotlin.concurrent.thread
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.PoiList

object AudioNavigationClientFabric: ClientModInitializer {
  override fun onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(PoiListPayload.ID, { payload: PoiListPayload, context: ClientPlayNetworking.Context ->
        thread { AudioNavigationClient.poiListQueue.put(payload.poiList) }
    })
    AudioNavigationClient.initialize()
  }
}
