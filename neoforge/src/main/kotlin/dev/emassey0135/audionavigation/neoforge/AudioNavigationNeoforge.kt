package dev.emassey0135.audionavigation.neoforge

import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.AudioNavigationFabricNeoforge

@Mod(AudioNavigation.MOD_ID)
class AudioNavigationNeoforge(bus: IEventBus, container: ModContainer) {
  init {
    Balm.initializeMod(AudioNavigation.MOD_ID, NeoForgeLoadContext(container, bus), AudioNavigationFabricNeoforge::initialize)
  }
}