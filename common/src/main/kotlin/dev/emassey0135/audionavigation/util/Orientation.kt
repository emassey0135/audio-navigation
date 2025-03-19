package dev.emassey0135.audionavigation.util

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sin
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Orientation(val verticalAngle: Double, val horizontalAngle: Double) {
  constructor (angles: Vec2): this(normalizeAngle(-angles.x.toDouble()), normalizeAngle(angles.y.toDouble()))
  fun toVector(): Vec3 {
    val verticalAngle = verticalAngle/180.0*PI
    val horizontalAngle = horizontalAngle/180.0*PI
    val x = sin(horizontalAngle)
    val y = sin(verticalAngle)
    val z = cos(horizontalAngle)
    return Vec3(x, y, z)
  }
  fun horizontalDifference(orientation: Orientation): Double {
    var angle1 = horizontalAngle
    var angle2 = orientation.horizontalAngle
    if (angle1<0.0)
      angle1+=360.0
    if (angle2<0.0)
      angle2+=360.0
    if (angle1>360.0)
      angle1-=360.0
    if (angle2>360.0)
      angle2-=360.0
    val result =  abs(angle1-angle2)
    return if (result>180.0) 360.0-result else result
  }
  companion object {
    private fun normalizeAngle(angle: Double): Double {
      var newAngle = angle%360.0
      if (newAngle<-180.0)
        newAngle+=360.0
      if (newAngle>180.0)
        newAngle-=360.0
      return newAngle
    }
    fun horizontalAngleBetween(pos1: BlockPos, pos2: BlockPos): Orientation {
      val newPos = pos2.subtract(pos1)
      val zeroAngle = atan2(1.0, 0.0)
      val angleInRadians = atan2(newPos.getZ().toDouble(), newPos.getX().toDouble())-zeroAngle
      var angleInDegrees = angleInRadians/PI*180
      if (angleInDegrees<-180.0)
        angleInDegrees+=360.0
      if (angleInDegrees>180.0)
        angleInDegrees-=360.0
      return Orientation(0.0, angleInDegrees.toDouble())
    }
  }
}
