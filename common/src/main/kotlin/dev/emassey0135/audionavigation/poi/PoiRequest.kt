package dev.emassey0135.audionavigation.poi

import java.util.Optional
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.math.BlockPos

data class PoiRequest(val pos: BlockPos, val radius: Int, val maxItems: Int, val verticalLimit: Optional<Int>, val type: Optional<PoiType>, val includedFeatures: Optional<List<String>>) {
  companion object {
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
    BlockPos.PACKET_CODEC, PoiRequest::pos,
    PacketCodecs.INTEGER, PoiRequest::radius,
    PacketCodecs.INTEGER, PoiRequest::maxItems,
    PacketCodecs.optional(PacketCodecs.INTEGER), PoiRequest::verticalLimit,
    PacketCodecs.optional(PoiType.PACKET_CODEC), PoiRequest::type,
    PacketCodecs.optional(PacketCodecs.STRING.collect(PacketCodecs.toList())), PoiRequest::includedFeatures,
    ::PoiRequest)
  }
}
