package dev.emassey0135.audionavigation.client.fabric

import net.blay09.mods.balm.client.BalmClient
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext
import net.fabricmc.api.ClientModInitializer;
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient

object AudioNavigationClientFabric: ClientModInitializer {
  override fun onInitializeClient() {
    BalmClient.initializeMod(AudioNavigation.MOD_ID, FabricLoadContext.INSTANCE, AudioNavigationClient::initialize)
  }
}
