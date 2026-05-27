package dev.emassey0135.audionavigation.fabricNeoforge

import java.util.UUID
import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.core.BalmRegistrars
import net.blay09.mods.balm.platform.attachment.DataAttachmentLookup
import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.config.ServerConfig
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Landmarks

object AudioNavigationFabricNeoforge {
  var WORLD_UUID_ATTACHMENT: DataAttachmentLookup<UUID>? = null
  fun initialize(registrars: BalmRegistrars) {
    registrars.dataAttachmentTypes { registrar ->
      WORLD_UUID_ATTACHMENT = registrar.register("world_uuid", UUIDUtil.CODEC, UUID::randomUUID).asLookup()
    }
    val networking = Balm.networking()
    networking.registerServerboundPacket(PoiRequestPayload.ID, PoiRequestPayload::class.java, PoiRequestPayload.CODEC as StreamCodec<RegistryFriendlyByteBuf, PoiRequestPayload>, { player: ServerPlayer, payload: PoiRequestPayload ->
      networking.reply(AudioNavigation.respondToPoiRequest(player.level(), player, payload))
    })
    networking.registerServerboundPacket(AddLandmarkPayload.ID, AddLandmarkPayload::class.java, AddLandmarkPayload.CODEC as StreamCodec<RegistryFriendlyByteBuf, AddLandmarkPayload>, { player: ServerPlayer, payload: AddLandmarkPayload ->
      Landmarks.addLandmark(player.level(), player, payload.name, payload.pos, payload.visibleToOtherPlayers)
    })
    networking.registerServerboundPacket(DeleteLandmarkPayload.ID, DeleteLandmarkPayload::class.java, DeleteLandmarkPayload.CODEC as StreamCodec<RegistryFriendlyByteBuf, DeleteLandmarkPayload>, { player: ServerPlayer, payload: DeleteLandmarkPayload ->
      Landmarks.deleteLandmark(payload.landmarkID)
    })
    LivingEntityCallback.Death.Before.EVENT.register { entity, _ ->
      if (entity is ServerPlayer)
        Landmarks.addLandmarkOnDeath(entity)
      true
    }
    ServerConfig.initialize()
    val config = ServerConfig.createServerConfiguration()
    AudioNavigation.initialize(AudioNavigationPlatformImpl(), config)
  }
}
