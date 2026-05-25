package dev.emassey0135.audionavigation.fabricNeoforge

import java.util.UUID
import net.blay09.mods.balm.core.BalmRegistrars
import net.blay09.mods.balm.platform.attachment.DataAttachmentLookup
import net.minecraft.core.UUIDUtil
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.config.ServerConfig

object AudioNavigationFabricNeoforge {
  var WORLD_UUID_ATTACHMENT: DataAttachmentLookup<UUID>? = null
  fun initialize(registrars: BalmRegistrars) {
    registrars.dataAttachmentTypes { registrar ->
      WORLD_UUID_ATTACHMENT = registrar.register("world_uuid", UUIDUtil.CODEC, UUID::randomUUID).asLookup()
    }
    ServerConfig.initialize()
    val config = ServerConfig.createServerConfiguration()
    AudioNavigation.initialize(AudioNavigationPlatformImpl(), config)
  }
}
