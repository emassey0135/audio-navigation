package dev.emassey0135.audionavigation.client.fabric

import net.blay09.mods.balm.client.BalmClient
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.PoiListPayload

object AudioNavigationClientFabric: ClientModInitializer {
  override fun onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(PoiListPayload.ID, { payload: PoiListPayload, context: ClientPlayNetworking.Context ->
        AudioNavigationClient.handlePoiList(payload)
    })
    BalmClient.initializeMod(AudioNavigation.MOD_ID, FabricLoadContext.INSTANCE, AudioNavigationClient::initialize)
  }
}
