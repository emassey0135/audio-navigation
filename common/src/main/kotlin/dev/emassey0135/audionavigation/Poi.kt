package dev.emassey0135.audionavigation

import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.Database

enum class PoiType {
  LANDMARK, FEATURE, STRUCTURE;
  companion object {
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
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiType.PACKET_CODEC, Poi::type,
      Identifier.PACKET_CODEC, Poi::identifier,
      BlockPos.PACKET_CODEC, Poi::pos,
      ::Poi)
  }
}

data class PoiAndDistance(val poi: Poi, val distance: Double) {
  override fun equals(poi2: Any?): Boolean {
    return (this === poi2) || ((poi2 is PoiAndDistance) && poi.equals(poi2.poi))
  }
  override fun hashCode(): Int {
    return poi.hashCode()
  }
  companion object {
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      Poi.PACKET_CODEC, PoiAndDistance::poi,
      PacketCodecs.DOUBLE, PoiAndDistance::distance,
      ::PoiAndDistance)
  }
}

class PoiList(list: List<PoiAndDistance>) {
  private val poiList = list.toMutableList()
  constructor (): this(listOf())
  fun toList(): List<PoiAndDistance> {
    return poiList.toList()
  }
  fun addPoi(poi: PoiAndDistance) {
    poiList.add(poi)
  }
  fun addPoi(poi: Poi, distance: Double) {
    poiList.add(PoiAndDistance(poi, distance))
  }
  fun subtract(poiList2: PoiList): PoiList {
    return PoiList(poiList-poiList2.toList())
  }
  companion object {
    fun getNearest(origin: BlockPos, radius: Double, maxItems: Int): PoiList {
      val statement = Database.connection.createStatement()
      val x = origin.getX().toDouble()
      val y = origin.getY().toDouble()
      val z = origin.getZ().toDouble()
      val results = statement.executeQuery("SELECT id, name, x, y, z, distance($x, $y, $z, x, y, z) AS distance FROM features WHERE distance <= $radius AND minX >= ${x-radius} AND maxX <= ${x+radius} AND minY >= ${y-radius} AND maxY <= ${y+radius} AND minZ >= ${z-radius} AND maxZ <= ${z+radius} ORDER BY distance LIMIT $maxItems")
      val poiList = PoiList()
      while (results.next()) {
        poiList.addPoi(Poi(PoiType.FEATURE, Identifier.of(results.getString("name")), BlockPos(results.getDouble("x").toInt(), results.getDouble("y").toInt(), results.getDouble("z").toInt())), results.getDouble("distance"))
      }
      return poiList
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiAndDistance.PACKET_CODEC.collect(PacketCodecs.toList()), PoiList::toList,
      ::PoiList)
  }
}
