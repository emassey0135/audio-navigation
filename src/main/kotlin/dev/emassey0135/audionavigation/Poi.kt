package dev.emassey0135.audionavigation

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.BlockPos

enum class PoiType {
  LANDMARK, FEATURE, STRUCTURE;
  companion object {
    @JvmField val CODEC = Codecs.rangedInt(0, 2).xmap({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
    @JvmField val PACKET_CODEC = PacketCodecs.indexed({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
  }
}

data class Poi(val type: PoiType, val identifier: Identifier, val pos: BlockPos) {
  fun distance(pos2: BlockPos): Double {
    return pos.getSquaredDistance(pos2)
  }
  fun distance(poi: Poi): Double {
    return distance(poi.pos)
  }
  companion object {
    @JvmField val CODEC = RecordCodecBuilder.create { instance ->
      instance.group(
        PoiType.CODEC.fieldOf("type").forGetter(Poi::type),
        Identifier.CODEC.fieldOf("identifier").forGetter(Poi::identifier),
        BlockPos.CODEC.fieldOf("pos").forGetter(Poi::pos))
        .apply(instance, ::Poi)
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiType.PACKET_CODEC, Poi::type,
      Identifier.PACKET_CODEC, Poi::identifier,
      BlockPos.PACKET_CODEC, Poi::pos,
      ::Poi)
  }
}

class PoiList(set: Set<Poi>) {
  private val poiList = set.toMutableSet()
  constructor (): this(setOf())
  constructor (list: List<Poi>): this(list.toMutableSet())
  fun toSet(): Set<Poi> {
    return poiList.toSet()
  }
  fun toList(): List<Poi> {
    return poiList.toList()
  }
  fun addPoi(poi: Poi) {
    poiList.add(poi)
  }
  fun addPoi(type: PoiType, identifier: Identifier, pos: BlockPos) {
    poiList.add(Poi(type, identifier, pos))
  }
  fun filterByDistance(origin: BlockPos, maxDistance: Float): PoiList {
    return PoiList(poiList.filter({ poi -> poi.distance(origin) <= maxDistance }))
  }
  fun sortByDistance(origin: BlockPos): List<Poi> {
    return poiList.sortedBy({ poi -> poi.distance(origin) })
  }
  fun subtract(poiList2: PoiList): PoiList {
    return PoiList(poiList-poiList2.toSet())
  }
  companion object {
    @JvmField val CODEC = RecordCodecBuilder.create { instance ->
      instance.group(
        Poi.CODEC.listOf().fieldOf("poiList").forGetter(PoiList::toList))
        .apply(instance, ::PoiList)
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      Poi.PACKET_CODEC.collect(PacketCodecs.toList()), PoiList::toList,
      ::PoiList)
  }
}
