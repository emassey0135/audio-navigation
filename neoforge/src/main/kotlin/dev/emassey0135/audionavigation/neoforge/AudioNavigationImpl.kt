package dev.emassey0135.audionavigation.neoforge

import java.util.UUID
import net.minecraft.server.world.ServerWorld
import dev.emassey0135.audionavigation.neoforge.AudioNavigationNeoforge

object AudioNavigationImpl {
  @JvmStatic fun getWorldUUID(world: ServerWorld): UUID {
    return world.getData(AudioNavigationNeoforge.WORLD_UUID_ATTACHMENT)
  }
}
