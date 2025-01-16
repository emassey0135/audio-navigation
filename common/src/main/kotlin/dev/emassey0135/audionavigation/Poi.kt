package dev.emassey0135.audionavigation

import java.sql.PreparedStatement
import java.util.concurrent.locks.ReentrantLock
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
    addToDatabaseMutex.lock()
    if (addToDatabaseStatement == null)
      addToDatabaseStatement = Database.updateConnection.prepareStatement("INSERT INTO features (id, minX, maxX, minY, maxY, minZ, maxZ, name, x, y, z) VALUES(NULL, ?1, ?1, ?2, ?2, ?3, ?3, ?4, ?1, ?2, ?3)")
    addToDatabaseStatement?.setDouble(1, pos.getX().toDouble())
    addToDatabaseStatement?.setDouble(2, pos.getY().toDouble())
    addToDatabaseStatement?.setDouble(3, pos.getZ().toDouble())
    addToDatabaseStatement?.setString(4, identifier.getPath())
    addToDatabaseStatement?.executeUpdate()
    addToDatabaseMutex.unlock()
  }
  companion object {
    private var addToDatabaseStatement: PreparedStatement? = null
    private val addToDatabaseMutex = ReentrantLock()
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
    fun getFromDatabase(query: PreparedStatement): PoiList {
      val results = query.executeQuery()
      val poiList = PoiList()
      while (results.next()) {
        poiList.addPoi(Poi(PoiType.FEATURE, Identifier.of(results.getString("name")), BlockPos(results.getDouble("x").toInt(), results.getDouble("y").toInt(), results.getDouble("z").toInt())), results.getDouble("distance"))
      }
      return poiList
    }
    var getNearestStatement: PreparedStatement? = null
    val getNearestMutex = ReentrantLock()
    fun getNearest(origin: BlockPos, radius: Double, maxItems: Int): PoiList {
      getNearestMutex.lock()
      if (getNearestStatement==null)
        getNearestStatement = Database.queryConnection.prepareStatement("SELECT id, name, x, y, z, distance(?1, ?2, ?3, x, y, z) AS distance FROM features WHERE distance <= ?4 AND minX >= ?1-?4 AND maxX <= ?1+?4 AND minY >= ?2-?4 AND maxY <= ?2+?4 AND minZ >= ?3-?4 AND maxZ <= ?3+?4 ORDER BY distance LIMIT ?5")
      getNearestStatement?.setDouble(1, origin.getX().toDouble())
      getNearestStatement?.setDouble(2, origin.getY().toDouble())
      getNearestStatement?.setDouble(3, origin.getZ().toDouble())
      getNearestStatement?.setDouble(4, radius)
      getNearestStatement?.setInt(5, maxItems)
      val result = getFromDatabase(getNearestStatement!!)
      getNearestMutex.unlock()
      return result
    }
    var getNearestWithVerticalLimitStatement: PreparedStatement? = null
    val getNearestWithVerticalLimitMutex = ReentrantLock()
    fun getNearestWithVerticalLimit(origin: BlockPos, radius: Double, maxItems: Int, verticalLimit: Double): PoiList {
      getNearestWithVerticalLimitMutex.lock()
      if (getNearestWithVerticalLimitStatement==null)
        getNearestWithVerticalLimitStatement = Database.queryConnection.prepareStatement("SELECT id, name, x, y, z, distance(?1, ?2, ?3, x, y, z) AS distance FROM features WHERE y >= ?2-?6 AND y <= ?2+?6 AND distance <= ?4 AND minX >= ?1-?4 AND maxX <= ?1+?4 AND minY >= ?2-?6 AND maxY <= ?2+?6 AND minZ >= ?3-?4 AND maxZ <= ?3+?4 ORDER BY distance LIMIT ?5")
      getNearestWithVerticalLimitStatement?.setDouble(1, origin.getX().toDouble())
      getNearestWithVerticalLimitStatement?.setDouble(2, origin.getY().toDouble())
      getNearestWithVerticalLimitStatement?.setDouble(3, origin.getZ().toDouble())
      getNearestWithVerticalLimitStatement?.setDouble(4, radius)
      getNearestWithVerticalLimitStatement?.setInt(5, maxItems)
      getNearestWithVerticalLimitStatement?.setDouble(6, verticalLimit)
      val result = getFromDatabase(getNearestWithVerticalLimitStatement!!)
      getNearestWithVerticalLimitMutex.unlock()
      return result
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiAndDistance.PACKET_CODEC.collect(PacketCodecs.toList()), PoiList::toList,
      ::PoiList)
  }
}
