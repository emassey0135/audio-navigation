package dev.emassey0135.audionavigation

import org.locationtech.jts.geom.Point
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Database
import dev.emassey0135.audionavigation.Geometry

enum class PoiType {
  LANDMARK, FEATURE, STRUCTURE;
  companion object {
    @JvmField val CODEC = Codecs.rangedInt(0, 2).xmap({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
    @JvmField val PACKET_CODEC = PacketCodecs.indexed({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
  }
}

data class Poi(val type: PoiType, val identifier: Identifier, val point: Point) {
  constructor (type: PoiType, identifier: Identifier, pos: BlockPos): this(type, identifier, Geometry.blockPosToPoint(pos))
  fun toBlockPos(): BlockPos {
    return Geometry.pointToBlockPos(point)
  }
  fun distance(point2: Point): Double {
    return point.distance(point2)
  }
  fun distance(poi: Poi): Double {
    return distance(poi.point)
  }
  fun distance(pos: BlockPos): Double {
    return distance(Geometry.blockPosToPoint(pos))
  }
  fun addToDatabase() {
    check(type==PoiType.FEATURE)
    val statement = Database.connection.createStatement()
    val wkt = Geometry.writeWKT(point)
    statement.executeUpdate("INSERT INTO features (id, name, location) VALUES(NULL, '${identifier.getPath()}', ST_GeomFromText('${wkt}', -1))")
  }
  companion object {
    @JvmField val CODEC = RecordCodecBuilder.create { instance ->
      instance.group(
        PoiType.CODEC.fieldOf("type").forGetter(Poi::type),
        Identifier.CODEC.fieldOf("identifier").forGetter(Poi::identifier),
        BlockPos.CODEC.fieldOf("pos").forGetter(Poi::toBlockPos))
        .apply(instance, ::Poi)
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiType.PACKET_CODEC, Poi::type,
      Identifier.PACKET_CODEC, Poi::identifier,
      BlockPos.PACKET_CODEC, Poi::toBlockPos,
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
    fun getNearest(origin: Point, radius: Double): PoiList {
      val statement = Database.connection.createStatement()
      val originWKT = Geometry.writeWKT(origin)
      val results = statement.executeQuery("SELECT a.pos AS rank, b.id, b.name, ST_AsText(b.location) AS location, a.distance_crs AS distance FROM knn2 AS a JOIN features AS b ON (b.id = a.fid) WHERE f_table_name = 'features' AND ref_geometry = ST_GeomFromText('${originWKT}', -1) AND radius = ${radius} AND max_items = 10")
      val poiList = PoiList()
      while (results.next()) {
        poiList.addPoi(Poi(PoiType.FEATURE, Identifier.of(results.getString("name")), Geometry.readWKT(results.getString("location")) as Point))
      }
      return poiList
    }
    fun getNearest(origin: BlockPos, radius: Double): PoiList {
    return getNearest(Geometry.blockPosToPoint(origin), radius)
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
