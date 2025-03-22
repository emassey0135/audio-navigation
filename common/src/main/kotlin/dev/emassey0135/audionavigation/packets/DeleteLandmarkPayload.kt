package dev.emassey0135.audionavigation.packets

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class DeleteLandmarkPayload(val landmarkID: Int): CustomPacketPayload {
  override fun type(): CustomPacketPayload.Type<DeleteLandmarkPayload> {
    return ID
  }
  companion object {
    val ID = CustomPacketPayload.Type<DeleteLandmarkPayload>(PacketIdentifiers.DELETE_LANDMARK_ID)
    @JvmField val CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, DeleteLandmarkPayload::landmarkID,
      ::DeleteLandmarkPayload)
  }
}
