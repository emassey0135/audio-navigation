package dev.emassey0135.audionavigation.fabric

import java.util.UUID
import net.minecraft.server.world.ServerWorld
import dev.emassey0135.audionavigation.fabric.AudioNavigationFabric

object AudioNavigationImpl {
  @JvmStatic fun getWorldUUID(world: ServerWorld): UUID {
    return world.getAttachedOrCreate(AudioNavigationFabric.WORLD_UUID_ATTACHMENT)
  }
}
