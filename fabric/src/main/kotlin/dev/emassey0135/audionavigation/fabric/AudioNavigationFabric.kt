package dev.emassey0135.audionavigation.fabric

import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext
import net.fabricmc.api.ModInitializer;
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.AudioNavigationFabricNeoforge

object AudioNavigationFabric: ModInitializer {
  override fun onInitialize() {
    Balm.initializeMod(AudioNavigation.MOD_ID, FabricLoadContext.INSTANCE, AudioNavigationFabricNeoforge::initialize)
  }
}
