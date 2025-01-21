package dev.emassey0135.audionavigation.packets;

import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Uuids;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;

public record PoiRequestPayload(UUID requestID, BlockPos pos, Double radius, Integer maxItems, Boolean enableVerticalLimit, Double verticalLimit) implements CustomPayload {
  public static final CustomPayload.Id<PoiRequestPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_REQUEST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiRequestPayload> CODEC = PacketCodec.tuple(
    Uuids.PACKET_CODEC, PoiRequestPayload::requestID,
    BlockPos.PACKET_CODEC, PoiRequestPayload::pos,
    PacketCodecs.DOUBLE, PoiRequestPayload::radius,
    PacketCodecs.INTEGER, PoiRequestPayload::maxItems,
    PacketCodecs.BOOLEAN, PoiRequestPayload::enableVerticalLimit,
    PacketCodecs.DOUBLE, PoiRequestPayload::verticalLimit,
    PoiRequestPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
