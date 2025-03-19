package dev.emassey0135.audionavigation.neoforge.client

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient

@Mod(value=AudioNavigation.MOD_ID, dist=arrayOf(Dist.CLIENT)) object AudioNavigationClientNeoforge {
  init {
    AudioNavigationClient.initialize()
  }
}