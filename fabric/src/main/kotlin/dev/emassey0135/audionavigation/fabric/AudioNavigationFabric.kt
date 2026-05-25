package dev.emassey0135.audionavigation.fabric

import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.fabricNeoforge.AudioNavigationFabricNeoforge
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Landmarks

object AudioNavigationFabric: ModInitializer {
  override fun onInitialize() {
    PayloadTypeRegistry.serverboundPlay().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.clientboundPlay().register(PoiListPayload.ID, PoiListPayload.CODEC)
    PayloadTypeRegistry.serverboundPlay().register(AddLandmarkPayload.ID, AddLandmarkPayload.CODEC)
    PayloadTypeRegistry.serverboundPlay().register(DeleteLandmarkPayload.ID, DeleteLandmarkPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        context.responseSender().sendPacket(AudioNavigation.respondToPoiRequest(context.player().level(), context.player(), payload))
      })
    ServerPlayNetworking.registerGlobalReceiver(AddLandmarkPayload.ID, { payload: AddLandmarkPayload, context: ServerPlayNetworking.Context ->
        Landmarks.addLandmark(context.player().level(), context.player(), payload.name, payload.pos, payload.visibleToOtherPlayers)
      })
    ServerPlayNetworking.registerGlobalReceiver(DeleteLandmarkPayload.ID, { payload: DeleteLandmarkPayload, context: ServerPlayNetworking.Context ->
        Landmarks.deleteLandmark(payload.landmarkID)
      })
    Balm.initializeMod(AudioNavigation.MOD_ID, FabricLoadContext.INSTANCE, AudioNavigationFabricNeoforge::initialize)
  }
}
