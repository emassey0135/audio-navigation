package dev.emassey0135.audionavigation.neoforge

import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.AudioNavigationClient
import dev.emassey0135.audionavigation.fabricNeoforge.AudioNavigationFabricNeoforge
import dev.emassey0135.audionavigation.fabricNeoforge.config.ServerConfig
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Landmarks

@Mod(AudioNavigation.MOD_ID)
@EventBusSubscriber
class AudioNavigationNeoforge(private val bus: IEventBus, container: ModContainer) {
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
    Balm.initializeMod(AudioNavigation.MOD_ID, NeoForgeLoadContext(container, bus), AudioNavigationFabricNeoforge::initialize)
  }
}