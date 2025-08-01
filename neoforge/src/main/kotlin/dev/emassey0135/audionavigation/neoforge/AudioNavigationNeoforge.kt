package dev.emassey0135.audionavigation.neoforge

import java.util.UUID
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient
import dev.emassey0135.audionavigation.fabricNeoforge.config.ServerConfig
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Landmarks

@Mod(AudioNavigation.MOD_ID)
@EventBusSubscriber
object AudioNavigationNeoforge {
  private val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AudioNavigation.MOD_ID)
  val WORLD_UUID_ATTACHMENT = ATTACHMENT_TYPES.register("world_uuid", fun(): AttachmentType<UUID> {
     return AttachmentType.builder(fun(): UUID { return UUID.randomUUID() }).serialize(UUIDUtil.CODEC.fieldOf("uuid")).build()
  })
  @SubscribeEvent fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
    val registrar = event.registrar("1")
    registrar.playToServer(PoiRequestPayload.ID, PoiRequestPayload.CODEC, { payload: PoiRequestPayload, context: IPayloadContext ->
        context.reply(AudioNavigation.respondToPoiRequest(context.player().level() as ServerLevel, context.player() as ServerPlayer, payload))
      })
    registrar.playToClient(PoiListPayload.ID, PoiListPayload.CODEC, { payload: PoiListPayload, context: IPayloadContext ->
        AudioNavigationClient.handlePoiList(payload)
      })
    registrar.playToServer(AddLandmarkPayload.ID, AddLandmarkPayload.CODEC, { payload: AddLandmarkPayload, context: IPayloadContext ->
        Landmarks.addLandmark(context.player().level() as ServerLevel, context.player() as ServerPlayer, payload.name, payload.pos, payload.visibleToOtherPlayers)
      })
    registrar.playToServer(DeleteLandmarkPayload.ID, DeleteLandmarkPayload.CODEC, { payload: DeleteLandmarkPayload, context: IPayloadContext ->
        Landmarks.deleteLandmark(payload.landmarkID)
      })
  }
  init {
    ATTACHMENT_TYPES.register(MOD_BUS)
    ServerConfig.initialize()
    val config = ServerConfig.createServerConfiguration()
    AudioNavigation.initialize(AudioNavigationPlatformImpl(), config)
  }
}