package dev.emassey0135.audionavigation.fabricNeoforge

import java.util.UUID
import net.minecraft.server.level.ServerLevel
import dev.emassey0135.audionavigation.AudioNavigationPlatform

class AudioNavigationPlatformImpl(): AudioNavigationPlatform {
  override fun getWorldUUID(world: ServerLevel): UUID {
    return AudioNavigationFabricNeoforge.WORLD_UUID_ATTACHMENT!!.getOrCreate(world)
  }
}
