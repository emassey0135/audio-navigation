package dev.emassey0135.audionavigation.packets;

import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Uuids;
import dev.emassey0135.audionavigation.poi.PoiRequest;

public record PoiRequestPayload(UUID requestID, PoiRequest poiRequest) implements CustomPayload {
  public static final CustomPayload.Id<PoiRequestPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_REQUEST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiRequestPayload> CODEC = PacketCodec.tuple(
    Uuids.PACKET_CODEC, PoiRequestPayload::requestID,
    PoiRequest.PACKET_CODEC, PoiRequestPayload::poiRequest,
    PoiRequestPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
