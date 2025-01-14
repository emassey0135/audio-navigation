package dev.emassey0135.audionavigation.neoforge

import net.neoforged.neoforge.network.PacketDistributor
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationClientImpl {
  @JvmStatic fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    PacketDistributor.sendToServer(poiRequestPayload)
  }
}
