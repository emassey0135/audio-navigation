package dev.emassey0135.audionavigation.packets

import java.util.UUID
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import dev.emassey0135.audionavigation.poi.PoiList

class PoiListPayload(val requestID: UUID, val poiList: PoiList): CustomPacketPayload {
  override fun type(): CustomPacketPayload.Type<PoiListPayload> {
    return ID
  }
  companion object {
    @JvmField val ID = CustomPacketPayload.Type<PoiListPayload>(PacketIdentifiers.POI_LIST_ID)
    @JvmField val CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, PoiListPayload::requestID,
      PoiList.STREAM_CODEC, PoiListPayload::poiList,
      ::PoiListPayload)
  }
}
