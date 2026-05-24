package dev.emassey0135.audionavigation.paper

import java.util.UUID
import net.minecraft.server.level.ServerLevel
import dev.emassey0135.audionavigation.AudioNavigationPlatform

class AudioNavigationPlatformImpl(): AudioNavigationPlatform {
  override fun getWorldUUID(world: ServerLevel): UUID {
    val container = world.getWorld().getPersistentDataContainer()
    if (container.has(AudioNavigationPaper.WORLD_UUID_KEY, UUIDDataType())) {
      return container.get(AudioNavigationPaper.WORLD_UUID_KEY, UUIDDataType())!!
    }
    else {
      val uuid = UUID.randomUUID()
      container.set(AudioNavigationPaper.WORLD_UUID_KEY, UUIDDataType(), uuid)
      return uuid
    }
  }
}
