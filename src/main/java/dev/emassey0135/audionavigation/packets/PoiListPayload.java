package dev.emassey0135.audionavigation.packets;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import dev.emassey0135.audionavigation.PoiList;
import dev.emassey0135.audionavigation.packets.PacketIdentifiers;

public record PoiListPayload(PoiList poiList) implements CustomPayload {
  public static final CustomPayload.Id<PoiListPayload> ID = new CustomPayload.Id<>(PacketIdentifiers.POI_LIST_ID);
  public static final PacketCodec<RegistryByteBuf, PoiListPayload> CODEC = PacketCodec.tuple(PoiList.PACKET_CODEC, PoiListPayload::poiList, PoiListPayload::new);
  @Override public CustomPayload.Id<? extends CustomPayload> getId() {
    return ID;
  }
}
