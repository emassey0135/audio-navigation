package dev.emassey0135.audionavigation.client.neoforge

import net.neoforged.neoforge.client.network.ClientPacketDistributor
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationClientImpl {
  @JvmStatic fun sendAddLandmark(addLandmarkPayload: AddLandmarkPayload) {
    ClientPacketDistributor.sendToServer(addLandmarkPayload)
  }
  @JvmStatic fun sendDeleteLandmark(deleteLandmarkPayload: DeleteLandmarkPayload) {
    ClientPacketDistributor.sendToServer(deleteLandmarkPayload)
  }
  @JvmStatic fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    ClientPacketDistributor.sendToServer(poiRequestPayload)
  }
}
