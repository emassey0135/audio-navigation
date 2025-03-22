package dev.emassey0135.audionavigation.packets

import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class AddLandmarkPayload(val name: String, val pos: BlockPos): CustomPacketPayload {
  override fun type(): CustomPacketPayload.Type<AddLandmarkPayload> {
    return ID
  }
  companion object {
    @JvmField val ID = CustomPacketPayload.Type<AddLandmarkPayload>(PacketIdentifiers.ADD_LANDMARK_ID)
    @JvmField val CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, AddLandmarkPayload::name,
      BlockPos.STREAM_CODEC, AddLandmarkPayload::pos,
      ::AddLandmarkPayload)
  }
}
