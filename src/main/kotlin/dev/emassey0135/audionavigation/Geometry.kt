package dev.emassey0135.audionavigation

import kotlin.math.round
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.io.WKTWriter
import net.minecraft.util.math.BlockPos

object Geometry {
  val geometryFactory = GeometryFactory(PrecisionModel(), -1)
  val wktReader = WKTReader(geometryFactory)
  val wktWriter = WKTWriter(3)
  fun pointToBlockPos(point: Point): BlockPos {
    val coord = point.getCoordinate()
    return BlockPos(round(coord.x).toInt(), round(coord.z).toInt(), round(coord.y).toInt())
  }
  fun blockPosToPoint(pos: BlockPos): Point {
    return geometryFactory.createPoint(Coordinate(pos.getX().toDouble(), pos.getZ().toDouble(), pos.getY().toDouble()))
  }
  fun writeWKT(geometry: Geometry): String {
    return wktWriter.write(geometry)
  }
  fun readWKT(wkt: String): Geometry {
    return wktReader.read(wkt)
  }
}
