package dev.emassey0135.audionavigation.fabric

import java.util.UUID
import net.minecraft.server.level.ServerLevel
import dev.emassey0135.audionavigation.AudioNavigationPlatform

class AudioNavigationPlatformImpl(): AudioNavigationPlatform {
  override fun getWorldUUID(world: ServerLevel): UUID {
    return world.getAttachedOrCreate(AudioNavigationFabric.WORLD_UUID_ATTACHMENT)
  }
}
