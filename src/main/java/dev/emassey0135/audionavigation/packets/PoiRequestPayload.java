package dev.emassey0135.audionavigation.packets;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.math.BlockPos;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;

public record PoiRequestPayload(BlockPos pos) implements CustomPayload {
  public static final CustomPayload.Id<PoiRequestPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_REQUEST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiRequestPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, PoiRequestPayload::pos, PoiRequestPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}