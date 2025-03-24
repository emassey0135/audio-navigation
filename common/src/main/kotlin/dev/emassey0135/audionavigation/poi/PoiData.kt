package dev.emassey0135.audionavigation.poi

import java.util.UUID
import kotlinx.serialization.Serializable
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

@Serializable data class PoiData(val player: UUID, val visibleToOtherPlayers: Boolean) {
  companion object {
    @JvmField val STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, PoiData::player,
      ByteBufCodecs.BOOL, PoiData::visibleToOtherPlayers,
      ::PoiData)
  }
}
