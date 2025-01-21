package dev.emassey0135.audionavigation.packets;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;

public record DeleteLandmarkPayload(Integer landmarkID) implements CustomPayload {
  public static final CustomPayload.Id<DeleteLandmarkPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.DELETE_LANDMARK_ID);
  public static final PacketCodec<RegistryByteBuf, DeleteLandmarkPayload> CODEC = PacketCodec.tuple(
    PacketCodecs.INTEGER, DeleteLandmarkPayload::landmarkID,
    DeleteLandmarkPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
