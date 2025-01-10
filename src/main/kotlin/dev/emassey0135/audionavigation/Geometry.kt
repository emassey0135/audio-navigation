package dev.emassey0135.audionavigation

import kotlin.math.round
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.WKBWriter
import net.minecraft.util.math.BlockPos

object Geometry {
  val geometryFactory = GeometryFactory(PrecisionModel(), -1)
  val wkbWriter = WKBWriter(3)
  fun pointToBlockPos(point: Point): BlockPos {
    val coord = point.getCoordinate()
    return BlockPos(round(coord.x).toInt(), round(coord.y).toInt(), round(coord.z).toInt())
  }
  fun blockPosToPoint(pos: BlockPos): Point {
    return geometryFactory.createPoint(Coordinate(pos.getX().toDouble(), pos.getY().toDouble(), pos.getZ().toDouble()))
  }
  fun geometryToWKBHex(geometry: Geometry): String {
    return WKBWriter.toHex(wkbWriter.write(geometry))
  }
}
