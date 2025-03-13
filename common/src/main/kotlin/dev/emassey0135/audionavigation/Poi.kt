package dev.emassey0135.audionavigation

import java.sql.PreparedStatement
import java.util.concurrent.locks.ReentrantLock
import java.util.Optional
import kotlin.math.abs
import net.minecraft.client.resource.language.I18n
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.Uuids
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Database

enum class PoiType {
  LANDMARK, FEATURE, STRUCTURE;
  companion object {
    @JvmField val PACKET_CODEC = PacketCodecs.indexed({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
  }
}

data class Poi(val type: PoiType, val name: String, val pos: BlockPos) {
  fun distance(pos2: BlockPos): Double {
    return pos.getSquaredDistance(pos2)
  }
  fun distance(poi: Poi): Double {
    return distance(poi.pos)
  }
  fun addToDatabase(world: ServerWorld) {
    addToDatabaseMutex.lock()
    if (addToDatabaseStatement == null)
      addToDatabaseStatement = Database.connection.prepareStatement("INSERT INTO pois (id, minX, maxX, minY, maxY, minZ, maxZ, world, type, name, x, y, z) VALUES(NULL, ?1, ?1, ?2, ?2, ?3, ?3, ?6, ?4, ?5, ?1, ?2, ?3)")
    addToDatabaseStatement?.setDouble(1, pos.getX().toDouble())
    addToDatabaseStatement?.setDouble(2, pos.getY().toDouble())
    addToDatabaseStatement?.setDouble(3, pos.getZ().toDouble())
    addToDatabaseStatement?.setInt(4, type.ordinal)
    addToDatabaseStatement?.setString(5, name)
    addToDatabaseStatement?.setBytes(6, Uuids.toByteArray(AudioNavigation.getWorldUUID(world)))
    addToDatabaseStatement?.executeUpdate()
    addToDatabaseMutex.unlock()
    Database.scheduleCommitIfNeeded()
  }
  fun positionAsString(): String {
    return "(${pos.getX().toString()}, ${pos.getY().toString()}, ${pos.getZ().toString()})"
  }
  fun positionAsNarratableString(): String {
    val x = pos.getX()
    val xString = if (x<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -x) else x.toString()
    val y = pos.getY()
    val yString = if (y<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -y) else y.toString()
    val z = pos.getZ()
    val zString = if (z<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -z) else z.toString()
    return "($xString, $yString, $zString)"
  }
  companion object {
    private var addToDatabaseStatement: PreparedStatement? = null
    private val addToDatabaseMutex = ReentrantLock()
    private var deleteLandmarkStatement: PreparedStatement? = null
    private val deleteLandmarkMutex = ReentrantLock()
    fun deleteLandmark(id: Int) {
      deleteLandmarkMutex.lock()
      if (deleteLandmarkStatement==null)
        deleteLandmarkStatement = Database.connection.prepareStatement("DELETE FROM pois WHERE id = ?1")
      deleteLandmarkStatement?.setInt(1, id)
      deleteLandmarkStatement?.executeUpdate()
      deleteLandmarkMutex.unlock()
      Database.scheduleCommitIfNeeded()
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiType.PACKET_CODEC, Poi::type,
      PacketCodecs.STRING, Poi::name,
      BlockPos.PACKET_CODEC, Poi::pos,
      ::Poi)
  }
}

data class PoiListItem(val poi: Poi, val distance: Double, val id: Int) {
  override fun equals(poi2: Any?): Boolean {
    return (this === poi2) || ((poi2 is PoiListItem) && id.equals(poi2.id))
  }
  override fun hashCode(): Int {
    return id.hashCode()
  }
  companion object {
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      Poi.PACKET_CODEC, PoiListItem::poi,
      PacketCodecs.DOUBLE, PoiListItem::distance,
      PacketCodecs.INTEGER, PoiListItem::id,
      ::PoiListItem)
  }
}
data class PoiRequest(val pos: BlockPos, val radius: Double, val maxItems: Int, val verticalLimit: Optional<Double>, val type: Optional<PoiType>, val includedFeatures: Optional<List<String>>) {
  companion object {
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
    BlockPos.PACKET_CODEC, PoiRequest::pos,
    PacketCodecs.DOUBLE, PoiRequest::radius,
    PacketCodecs.INTEGER, PoiRequest::maxItems,
    PacketCodecs.optional(PacketCodecs.DOUBLE), PoiRequest::verticalLimit,
    PacketCodecs.optional(PoiType.PACKET_CODEC), PoiRequest::type,
    PacketCodecs.optional(PacketCodecs.STRING.collect(PacketCodecs.toList())), PoiRequest::includedFeatures,
    ::PoiRequest)
  }
}
class PoiList(list: List<PoiListItem>) {
  private val poiList = list.toMutableList()
  constructor (): this(listOf())
  fun toList(): List<PoiListItem> {
    return poiList.toList()
  }
  fun addPoi(poi: PoiListItem) {
    poiList.add(poi)
  }
  fun addPoi(poi: Poi, distance: Double, id: Int) {
    poiList.add(PoiListItem(poi, distance, id))
  }
  fun delete(id: Int) {
    poiList.removeAll(poiList.filter { poi -> poi.id==id })
  }
  fun subtract(poiList2: PoiList): PoiList {
    return PoiList(poiList-poiList2.toList())
  }
  companion object {
    var currentPoiRequest: PoiRequest? = null
    fun filterPoi(poi: Poi): Double {
      val pos = currentPoiRequest!!.pos
      val radius = currentPoiRequest!!.radius
      val verticalLimit = currentPoiRequest!!.verticalLimit
      val type = currentPoiRequest!!.type
      val includedFeatures = currentPoiRequest!!.includedFeatures
      val distance = poi.distance(pos)
      return when {
        distance > radius -> -1.0
        verticalLimit.isPresent() && (abs(poi.pos.getY()-pos.getY()) > verticalLimit.get()) -> -1.0
        type.isPresent() && (poi.type != type.get()) -> -1.0
        includedFeatures.isPresent() && (poi.type == PoiType.FEATURE) && (poi.name !in includedFeatures.get()) -> -1.0
        else -> distance
      }
    }
    private fun getFromDatabase(query: PreparedStatement): PoiList {
      val poiList = PoiList()
      query.executeQuery().use {
        while (it.next()) {
          poiList.addPoi(Poi(PoiType.entries.get(it.getInt("type")), it.getString("name"), BlockPos(it.getDouble("x").toInt(), it.getDouble("y").toInt(), it.getDouble("z").toInt())), it.getDouble("distance"), it.getInt("id"))
        }
      }
      return poiList
    }
    var getNearestStatement: PreparedStatement? = null
    val getNearestMutex = ReentrantLock()
    fun getNearest(world: ServerWorld, poiRequest: PoiRequest): PoiList {
      getNearestMutex.lock()
      currentPoiRequest = poiRequest
      if (getNearestStatement==null)
        getNearestStatement = Database.connection.prepareStatement("SELECT id, type, name, x, y, z, filterPoi(type, name, x, y, z) AS distance FROM pois WHERE distance >= 0 AND world = ?6 AND minX >= ?1-?4 AND maxX <= ?1+?4 AND minY >= ?2-?4 AND maxY <= ?2+?4 AND minZ >= ?3-?4 AND maxZ <= ?3+?4 ORDER BY distance LIMIT ?5")
      getNearestStatement?.setDouble(1, poiRequest.pos.getX().toDouble())
      getNearestStatement?.setDouble(2, poiRequest.pos.getY().toDouble())
      getNearestStatement?.setDouble(3, poiRequest.pos.getZ().toDouble())
      getNearestStatement?.setDouble(4, poiRequest.radius)
      getNearestStatement?.setInt(5, poiRequest.maxItems)
      getNearestStatement?.setBytes(6, Uuids.toByteArray(AudioNavigation.getWorldUUID(world)))
      val result = getFromDatabase(getNearestStatement!!)
      getNearestMutex.unlock()
      return result
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiListItem.PACKET_CODEC.collect(PacketCodecs.toList()), PoiList::toList,
      ::PoiList)
  }
}
