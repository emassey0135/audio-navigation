package dev.emassey0135.audionavigation.packets;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Uuids;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;
import dev.emassey0135.audionavigation.PoiType;

public record PoiRequestPayload(UUID requestID, BlockPos pos, Double radius, Integer maxItems, Boolean enableVerticalLimit, Optional<Double> verticalLimit, Boolean filterByType, Optional<PoiType> type) implements CustomPayload {
  public static final CustomPayload.Id<PoiRequestPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_REQUEST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiRequestPayload> CODEC = PacketCodec.tuple(
    Uuids.PACKET_CODEC, PoiRequestPayload::requestID,
    BlockPos.PACKET_CODEC, PoiRequestPayload::pos,
    PacketCodecs.DOUBLE, PoiRequestPayload::radius,
    PacketCodecs.INTEGER, PoiRequestPayload::maxItems,
    PacketCodecs.BOOLEAN, PoiRequestPayload::enableVerticalLimit,
    PacketCodecs.optional(PacketCodecs.DOUBLE), PoiRequestPayload::verticalLimit,
    PacketCodecs.BOOLEAN, PoiRequestPayload::filterByType,
    PacketCodecs.optional(PoiType.PACKET_CODEC), PoiRequestPayload::type,
    PoiRequestPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
