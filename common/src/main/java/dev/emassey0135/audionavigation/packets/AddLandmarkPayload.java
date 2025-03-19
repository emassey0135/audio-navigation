package dev.emassey0135.audionavigation.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record AddLandmarkPayload(String name, BlockPos pos) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<AddLandmarkPayload> ID = new CustomPacketPayload.Type<>(PacketIdentifiers.ADD_LANDMARK_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, AddLandmarkPayload> CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8, AddLandmarkPayload::name,
    BlockPos.STREAM_CODEC, AddLandmarkPayload::pos,
    AddLandmarkPayload::new);
  @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}
