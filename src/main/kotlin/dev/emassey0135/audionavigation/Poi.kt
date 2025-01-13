package dev.emassey0135.audionavigation

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.Database

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
  fun addToDatabase() {
    check(type==PoiType.FEATURE)
    val statement = Database.connection.createStatement()
    val x = pos.getX().toDouble()
    val y = pos.getY().toDouble()
    val z = pos.getZ().toDouble()
    statement.executeUpdate("INSERT INTO features (id, minX, maxX, minY, maxY, minZ, maxZ, name, x, y, z) VALUES(NULL, $x, $x, $y, $y, $z, $z, '${identifier.getPath()}', $x, $y, $z)")
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

class PoiList(list: List<Poi>) {
  private val poiList = list.toMutableList()
  constructor (): this(listOf())
  fun toList(): List<Poi> {
    return poiList.toList()
  }
  fun addPoi(poi: Poi) {
    poiList.add(poi)
  }
  fun subtract(poiList2: PoiList): PoiList {
    return PoiList(poiList-poiList2.toList())
  }
  companion object {
    fun getNearest(origin: BlockPos, radius: Double): PoiList {
      val statement = Database.connection.createStatement()
      val x = origin.getX().toDouble()
      val y = origin.getY().toDouble()
      val z = origin.getZ().toDouble()
      val results = statement.executeQuery("SELECT id, name, x, y, z, distance($x, $y, $z, x, y, z) AS distance FROM features WHERE distance <= $radius AND minX >= ${x-radius} AND maxX <= ${x+radius} AND minY >= ${y-radius} AND maxY <= ${y+radius} AND minZ >= ${z-radius} AND maxZ <= ${z+radius} ORDER BY distance")
      val poiList = PoiList()
      while (results.next()) {
        poiList.addPoi(Poi(PoiType.FEATURE, Identifier.of(results.getString("name")), BlockPos(results.getDouble("x").toInt(), results.getDouble("y").toInt(), results.getDouble("z").toInt())))
      }
      return poiList
    }
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
