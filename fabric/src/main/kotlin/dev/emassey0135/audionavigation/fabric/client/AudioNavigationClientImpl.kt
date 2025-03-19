package dev.emassey0135.audionavigation.fabric.client

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationClientImpl {
  @JvmStatic fun sendAddLandmark(addLandmarkPayload: AddLandmarkPayload) {
    if (ClientPlayNetworking.canSend(AddLandmarkPayload.ID))
      ClientPlayNetworking.send(addLandmarkPayload)
  }
  @JvmStatic fun sendDeleteLandmark(deleteLandmarkPayload: DeleteLandmarkPayload) {
    if (ClientPlayNetworking.canSend(DeleteLandmarkPayload.ID))
      ClientPlayNetworking.send(deleteLandmarkPayload)
  }
  @JvmStatic fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    if (ClientPlayNetworking.canSend(PoiRequestPayload.ID))
      ClientPlayNetworking.send(poiRequestPayload)
  }
}
