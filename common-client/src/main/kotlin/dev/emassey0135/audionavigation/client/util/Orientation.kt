package dev.emassey0135.audionavigation.client.util

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
    var angle1 = normalizeAngleToPositive(horizontalAngle)
    var angle2 = normalizeAngleToPositive(orientation.horizontalAngle)
    val result =  abs(angle1-angle2)
    return if (result>180.0) 360.0-result else result
  }
  private fun horizontalSignedDifference(orientation: Orientation): Double {
    var angle1 = normalizeAngleToPositive(horizontalAngle)
    var angle2 = normalizeAngleToPositive(orientation.horizontalAngle)
    val result =  angle2-angle1
    return normalizeAngle(result)
  }
  private fun verticalSignedDifference(orientation: Orientation): Double {
    var angle1 = normalizeAngleToPositive(verticalAngle)
    var angle2 = normalizeAngleToPositive(orientation.verticalAngle)
    val result =  angle2-angle1
    return normalizeAngle(result)
  }
  fun signedDifference(orientation: Orientation): Orientation {
    return Orientation(this.verticalSignedDifference(orientation), this.horizontalSignedDifference(orientation))
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
    private fun normalizeAngleToPositive(angle: Double): Double {
      var newAngle = angle%360.0
      if (newAngle<0.0)
        newAngle+=360.0
      if (newAngle>360.0)
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
