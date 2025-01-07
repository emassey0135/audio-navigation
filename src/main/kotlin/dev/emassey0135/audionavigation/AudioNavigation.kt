package dev.emassey0135.audionavigation

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.PoiList

object AudioNavigation : ModInitializer {
  /* This code has been derived from code in Worldgen Devtools (https://github.com/jacobsjo/worldgen-devtools).
     Copyright (c) 2023
     See LICENSE.worldgen-devtools for more information.
  */
  @JvmField val POI_LIST_ATTACHMENT = AttachmentRegistry<PoiList>.create(
    Identifier.of("audionavigation", "poi_list"),
    { builder -> builder
      .initializer(::PoiList)
      .persistent(PoiList.CODEC)
      .syncWith(PoiList.PACKET_CODEC, { attachmentTarget, serverPlayer -> serverPlayer.hasPermissionLevel(2) })})

  @JvmField val logger = LoggerFactory.getLogger("audio-navigation")
  override fun onInitialize() {
    PayloadTypeRegistry.playC2S().register(PoiRequestPayload.ID, PoiRequestPayload.CODEC)
    PayloadTypeRegistry.playS2C().register(PoiListPayload.ID, PoiListPayload.CODEC)
    ServerPlayNetworking.registerGlobalReceiver(PoiRequestPayload.ID, { payload: PoiRequestPayload, context: ServerPlayNetworking.Context ->
        val poiList = context.player().getWorld().getChunk(payload.pos).getAttached(POI_LIST_ATTACHMENT)
        if (poiList!=null)
          context.responseSender().sendPacket(PoiListPayload(poiList))
      })
  }
}
