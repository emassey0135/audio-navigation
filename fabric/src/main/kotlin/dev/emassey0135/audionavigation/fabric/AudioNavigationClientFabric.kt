package dev.emassey0135.audionavigation.fabric

import net.fabricmc.api.ClientModInitializer;
import dev.emassey0135.audionavigation.AudioNavigationClient

object AudioNavigationClientFabric: ClientModInitializer {
  override fun onInitializeClient() {
    AudioNavigationClient.initialize()
  }
}
