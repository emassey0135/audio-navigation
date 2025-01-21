package dev.emassey0135.audionavigation.packets;

import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Uuids;
import dev.emassey0135.audionavigation.PoiList;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;

public record PoiListPayload(UUID requestID, PoiList poiList) implements CustomPayload {
  public static final CustomPayload.Id<PoiListPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_LIST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiListPayload> CODEC = PacketCodec.tuple(
    Uuids.PACKET_CODEC, PoiListPayload::requestID,
    PoiList.PACKET_CODEC, PoiListPayload::poiList,
    PoiListPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
