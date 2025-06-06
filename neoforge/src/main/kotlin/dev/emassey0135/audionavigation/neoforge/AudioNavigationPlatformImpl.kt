package dev.emassey0135.audionavigation.neoforge

import java.util.UUID
import net.minecraft.server.level.ServerLevel
import dev.emassey0135.audionavigation.AudioNavigationPlatform

class AudioNavigationPlatformImpl(): AudioNavigationPlatform {
  override fun getWorldUUID(world: ServerLevel): UUID {
    return world.getData(AudioNavigationNeoforge.WORLD_UUID_ATTACHMENT)
  }
}
