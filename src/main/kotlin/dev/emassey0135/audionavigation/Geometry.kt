package dev.emassey0135.audionavigation

import kotlin.math.round
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import net.minecraft.util.math.BlockPos

object Geometry {
  val geometryFactory = GeometryFactory(PrecisionModel(), -1)
  fun pointToBlockPos(point: Point): BlockPos {
    val coord = point.getCoordinate()
    return BlockPos(round(coord.x).toInt(), round(coord.y).toInt(), round(coord.z).toInt())
  }
  fun blockPosToPoint(pos: BlockPos): Point {
    return geometryFactory.createPoint(Coordinate(pos.getX().toDouble(), pos.getY().toDouble(), pos.getZ().toDouble()))
  }
}
