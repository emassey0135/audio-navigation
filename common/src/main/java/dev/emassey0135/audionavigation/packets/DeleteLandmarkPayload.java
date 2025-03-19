package dev.emassey0135.audionavigation.packets;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record DeleteLandmarkPayload(Integer landmarkID) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<DeleteLandmarkPayload> ID = new CustomPacketPayload.Type<>(PacketIdentifiers.DELETE_LANDMARK_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, DeleteLandmarkPayload> CODEC = StreamCodec.composite(
    ByteBufCodecs.INT, DeleteLandmarkPayload::landmarkID,
    DeleteLandmarkPayload::new);
  @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}
