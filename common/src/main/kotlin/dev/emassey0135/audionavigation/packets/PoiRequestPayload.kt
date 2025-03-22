package dev.emassey0135.audionavigation.packets

import java.util.UUID
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import dev.emassey0135.audionavigation.poi.PoiRequest

class PoiRequestPayload(val requestID: UUID, val poiRequest: PoiRequest): CustomPacketPayload {
  override fun type(): CustomPacketPayload.Type<PoiRequestPayload> {
    return ID
  }
  companion object {
    @JvmField val ID = CustomPacketPayload.Type<PoiRequestPayload>(PacketIdentifiers.POI_REQUEST_ID)
    @JvmField val CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, PoiRequestPayload::requestID,
      PoiRequest.STREAM_CODEC, PoiRequestPayload::poiRequest,
      ::PoiRequestPayload)
  }
}
