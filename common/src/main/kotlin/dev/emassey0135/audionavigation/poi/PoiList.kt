package dev.emassey0135.audionavigation.poi

import java.sql.PreparedStatement
import java.util.concurrent.locks.ReentrantLock
import java.util.UUID
import java.util.Optional
import kotlin.math.abs
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.util.Database

data class PoiListItem(val poi: Poi, val distance: Double, val id: Int) {
  override fun equals(poi2: Any?): Boolean {
    return (this === poi2) || ((poi2 is PoiListItem) && id.equals(poi2.id))
  }
  override fun hashCode(): Int {
    return id.hashCode()
  }
  companion object {
    @JvmField val STREAM_CODEC = StreamCodec.composite(
      Poi.STREAM_CODEC, PoiListItem::poi,
      ByteBufCodecs.DOUBLE, PoiListItem::distance,
      ByteBufCodecs.INT, PoiListItem::id,
      ::PoiListItem)
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
    var currentPlayerUUID: UUID? = null
    fun filterPoi(poi: Poi): Double {
      val pos = currentPoiRequest!!.pos
      val radius = currentPoiRequest!!.radius
      val verticalLimit = currentPoiRequest!!.verticalLimit
      val type = currentPoiRequest!!.type
      val includedFeatures = currentPoiRequest!!.includedFeatures
      val distance = poi.distance(pos)
      val config = AudioNavigation.config!!
      return when {
        type.isPresent() && (poi.type != type.get()) -> -1.0
        distance > radius -> -1.0
        distance > config.radiusLimit -> -1.0
        verticalLimit.isPresent() && (abs(poi.pos.getY()-pos.getY()) > verticalLimit.get()) -> -1.0
        (poi.type==PoiType.LANDMARK) && poi.data.isPresent() && !(poi.data.get().visibleToOtherPlayers) && (poi.data.get().player!=currentPlayerUUID) -> -1.0
        includedFeatures.isPresent() && (poi.type == PoiType.FEATURE) && (poi.name !in includedFeatures.get()) -> -1.0
        config.restrictFeatures && (poi.type == PoiType.FEATURE) && (poi.name !in config.allowedFeatures) -> -1.0
        else -> distance
      }
    }
    @OptIn(ExperimentalSerializationApi::class)
    private fun getFromDatabase(query: PreparedStatement): PoiList {
      val poiList = PoiList()
      query.executeQuery().use {
        while (it.next()) {
          val type = PoiType.entries.get(it.getInt("type"))
          val name = it.getString("name")
          val pos = BlockPos(it.getInt("x"), it.getInt("y"), it.getInt("z"))
          val dataBinary = it.getBytes("data")
          var data: Optional<PoiData> = Optional.empty()
          if (dataBinary!=null)
            data = Optional.of(ProtoBuf.decodeFromByteArray(dataBinary))
          val distance = it.getDouble("distance")
          val id = it.getInt("id")
          poiList.addPoi(Poi(type, name, pos, data), distance, id)
        }
      }
      return poiList
    }
    var getNearestStatement: PreparedStatement? = null
    val getNearestMutex = ReentrantLock()
    fun getNearest(world: ServerLevel, player: ServerPlayer, poiRequest: PoiRequest): PoiList {
      getNearestMutex.lock()
      currentPoiRequest = poiRequest
      currentPlayerUUID = player.getUUID()
      if (getNearestStatement==null)
        getNearestStatement = Database.connection.prepareStatement("SELECT id, type, name, data, x, y, z, filterPoi(type, name, x, y, z, data) AS distance FROM pois2 WHERE distance >= 0 AND world = ?6 AND minX >= ?1-?4 AND maxX <= ?1+?4 AND minY >= ?2-?4 AND maxY <= ?2+?4 AND minZ >= ?3-?4 AND maxZ <= ?3+?4 ORDER BY distance LIMIT ?5")
      getNearestStatement?.setInt(1, poiRequest.pos.getX())
      getNearestStatement?.setInt(2, poiRequest.pos.getY())
      getNearestStatement?.setInt(3, poiRequest.pos.getZ())
      getNearestStatement?.setInt(4, poiRequest.radius)
      getNearestStatement?.setInt(5, poiRequest.maxItems)
      getNearestStatement?.setBytes(6, UUIDUtil.uuidToByteArray(AudioNavigation.platform!!.getWorldUUID(world)))
      val result = getFromDatabase(getNearestStatement!!)
      getNearestMutex.unlock()
      return result
    }
    @JvmField val STREAM_CODEC = StreamCodec.composite(
      PoiListItem.STREAM_CODEC.apply(ByteBufCodecs.list()), PoiList::toList,
      ::PoiList)
  }
}
