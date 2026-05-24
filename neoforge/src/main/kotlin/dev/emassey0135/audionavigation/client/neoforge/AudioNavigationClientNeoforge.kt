package dev.emassey0135.audionavigation.client.neoforge

import net.blay09.mods.balm.client.BalmClient
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient

@Mod(value=AudioNavigation.MOD_ID, dist=arrayOf(Dist.CLIENT)) class AudioNavigationClientNeoforge(private val bus: IEventBus, private val container: ModContainer) {
  init {
    BalmClient.initializeMod(AudioNavigation.MOD_ID, NeoForgeLoadContext(container, bus), AudioNavigationClient::initialize)
  }
}