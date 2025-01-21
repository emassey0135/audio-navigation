package dev.emassey0135.audionavigation.fabric

import java.util.UUID
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.Uuids
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationFabric: ModInitializer {
  @JvmField val WORLD_UUID_ATTACHMENT = AttachmentRegistry.create(Identifier.of(AudioNavigation.MOD_ID, "world_uuid"), { builder ->
      builder.persistent(Uuids.CODEC).initializer({ UUID.randomUUID() })
  })
  override fun onInitialize() {
    PayloadTypeRegistry.playC2S().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.playS2C().register(PoiListPayload.ID, PoiListPayload.CODEC)
    PayloadTypeRegistry.playC2S().register(AddLandmarkPayload.ID, AddLandmarkPayload.CODEC)
    PayloadTypeRegistry.playC2S().register(DeleteLandmarkPayload.ID, DeleteLandmarkPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        context.responseSender().sendPacket(AudioNavigation.respondToPoiRequest(context.player().getWorld() as ServerWorld, payload))
      })
    ServerPlayNetworking.registerGlobalReceiver(AddLandmarkPayload.ID, { payload: AddLandmarkPayload, context: ServerPlayNetworking.Context ->
        AudioNavigation.addLandmark(context.player().getWorld() as ServerWorld, payload.name, payload.pos)
      })
    ServerPlayNetworking.registerGlobalReceiver(DeleteLandmarkPayload.ID, { payload: DeleteLandmarkPayload, context: ServerPlayNetworking.Context ->
        AudioNavigation.deleteLandmark(payload.landmarkID)
      })
    AudioNavigation.initialize()
  }
}
