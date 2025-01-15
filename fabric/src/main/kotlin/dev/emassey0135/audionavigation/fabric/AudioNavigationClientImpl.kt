package dev.emassey0135.audionavigation.fabric

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationClientImpl {
  @JvmStatic fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    if (ClientPlayNetworking.canSend(PoiRequestPayload.ID))
      ClientPlayNetworking.send(poiRequestPayload)
  }
}
