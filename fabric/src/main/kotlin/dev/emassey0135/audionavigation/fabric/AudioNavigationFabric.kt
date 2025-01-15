package dev.emassey0135.audionavigation.fabric

import net.fabricmc.api.ModInitializer;
import dev.emassey0135.audionavigation.AudioNavigation

object AudioNavigationFabric: ModInitializer {
  override fun onInitialize() {
    AudioNavigation.initialize()
  }
}
