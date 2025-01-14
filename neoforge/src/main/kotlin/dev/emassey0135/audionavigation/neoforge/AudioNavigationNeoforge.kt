package dev.emassey0135.audionavigation.neoforge

import net.neoforged.fml.common.Mod
import dev.emassey0135.audionavigation.AudioNavigation

@Mod(AudioNavigation.MOD_ID) object AudioNavigationNeoforge {
  init {
    AudioNavigation.initialize()
  }
}