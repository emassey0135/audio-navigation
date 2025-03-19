package dev.emassey0135.audionavigation.packets;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import dev.emassey0135.audionavigation.poi.PoiList;

public record PoiListPayload(UUID requestID, PoiList poiList) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<PoiListPayload> ID = new CustomPacketPayload.Type<>(PacketIdentifiers.POI_LIST_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, PoiListPayload> CODEC = StreamCodec.composite(
    UUIDUtil.STREAM_CODEC, PoiListPayload::requestID,
    PoiList.STREAM_CODEC, PoiListPayload::poiList,
    PoiListPayload::new);
  @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}
