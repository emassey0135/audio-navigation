package dev.emassey0135.audionavigation.poi

import java.util.UUID
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

object UUIDAsStringSerializer: KSerializer<UUID> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("dev.emassey0135.audionavigation.UUIDAsString", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}

@Serializable data class PoiData(
  @Serializable(with = UUIDAsStringSerializer::class) val player: UUID,
  val visibleToOtherPlayers: Boolean) {
  companion object {
    @JvmField val STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, PoiData::player,
      ByteBufCodecs.BOOL, PoiData::visibleToOtherPlayers,
      ::PoiData)
  }
}
