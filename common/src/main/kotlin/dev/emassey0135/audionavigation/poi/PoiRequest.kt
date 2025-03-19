package dev.emassey0135.audionavigation.poi

import java.util.Optional
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class PoiRequest(val pos: BlockPos, val radius: Int, val maxItems: Int, val verticalLimit: Optional<Int>, val type: Optional<PoiType>, val includedFeatures: Optional<List<String>>) {
  companion object {
    @JvmField val STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, PoiRequest::pos,
    ByteBufCodecs.INT, PoiRequest::radius,
    ByteBufCodecs.INT, PoiRequest::maxItems,
    ByteBufCodecs.optional(ByteBufCodecs.INT), PoiRequest::verticalLimit,
    ByteBufCodecs.optional(PoiType.STREAM_CODEC), PoiRequest::type,
    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())), PoiRequest::includedFeatures,
    ::PoiRequest)
  }
}
