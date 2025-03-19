package dev.emassey0135.audionavigation.packets;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import dev.emassey0135.audionavigation.poi.PoiRequest;

public record PoiRequestPayload(UUID requestID, PoiRequest poiRequest) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<PoiRequestPayload> ID = new CustomPacketPayload.Type<>(PacketIdentifiers.POI_REQUEST_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, PoiRequestPayload> CODEC = StreamCodec.composite(
    UUIDUtil.STREAM_CODEC, PoiRequestPayload::requestID,
    PoiRequest.STREAM_CODEC, PoiRequestPayload::poiRequest,
    PoiRequestPayload::new);
  @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}
