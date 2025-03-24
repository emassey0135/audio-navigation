package dev.emassey0135.audionavigation.poi

import java.nio.ByteBuffer
import java.util.UUID
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

object UUIDAsByteArraySerializer: KSerializer<UUID> {
  private val delegateSerializer = ByteArraySerializer()
  override val descriptor = SerialDescriptor("dev.emassey0135.audionavigation.UUIDAsByteArray", delegateSerializer.descriptor)
  override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeSerializableValue(delegateSerializer, UUIDUtil.uuidToByteArray(value))
  override fun deserialize(decoder: Decoder): UUID {
    val array = decoder.decodeSerializableValue(delegateSerializer)
    val buffer = ByteBuffer.wrap(array)
    val first = buffer.getLong()
    val second = buffer.getLong()
    return UUID(first, second)
  }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable data class PoiData(
  @ProtoNumber(1) @Serializable(with = UUIDAsByteArraySerializer::class) val player: UUID,
  @ProtoNumber(2) val visibleToOtherPlayers: Boolean) {
  companion object {
    @JvmField val STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, PoiData::player,
      ByteBufCodecs.BOOL, PoiData::visibleToOtherPlayers,
      ::PoiData)
  }
}
