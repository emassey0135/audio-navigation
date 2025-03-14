package dev.emassey0135.audionavigation.packets;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.math.BlockPos;

public record AddLandmarkPayload(String name, BlockPos pos) implements CustomPayload {
  public static final CustomPayload.Id<AddLandmarkPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.ADD_LANDMARK_ID);
  public static final PacketCodec<RegistryByteBuf, AddLandmarkPayload> CODEC = PacketCodec.tuple(
    PacketCodecs.STRING, AddLandmarkPayload::name,
    BlockPos.PACKET_CODEC, AddLandmarkPayload::pos,
    AddLandmarkPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
