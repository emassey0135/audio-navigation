package dev.emassey0135.audionavigation.fabric

import java.util.UUID
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel;
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.config.ServerConfig
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Landmarks

object AudioNavigationFabric: ModInitializer {
  @JvmField val WORLD_UUID_ATTACHMENT = AttachmentRegistry.create(ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "world_uuid"), { builder ->
      builder.persistent(UUIDUtil.CODEC).initializer({ UUID.randomUUID() })
  })
  override fun onInitialize() {
    PayloadTypeRegistry.playC2S().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.playS2C().register(PoiListPayload.ID, PoiListPayload.CODEC)
    PayloadTypeRegistry.playC2S().register(AddLandmarkPayload.ID, AddLandmarkPayload.CODEC)
    PayloadTypeRegistry.playC2S().register(DeleteLandmarkPayload.ID, DeleteLandmarkPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        context.responseSender().sendPacket(AudioNavigation.respondToPoiRequest(context.player().level() as ServerLevel, context.player(), payload))
      })
    ServerPlayNetworking.registerGlobalReceiver(AddLandmarkPayload.ID, { payload: AddLandmarkPayload, context: ServerPlayNetworking.Context ->
        Landmarks.addLandmark(context.player().level() as ServerLevel, context.player(), payload.name, payload.pos, payload.visibleToOtherPlayers)
      })
    ServerPlayNetworking.registerGlobalReceiver(DeleteLandmarkPayload.ID, { payload: DeleteLandmarkPayload, context: ServerPlayNetworking.Context ->
        Landmarks.deleteLandmark(payload.landmarkID)
      })
    ServerConfig.initialize()
    val config = ServerConfig.createServerConfiguration()
    AudioNavigation.initialize(AudioNavigationPlatformImpl(), config)
  }
}
