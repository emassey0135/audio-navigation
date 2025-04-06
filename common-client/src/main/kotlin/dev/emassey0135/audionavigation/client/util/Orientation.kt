package dev.emassey0135.audionavigation.client.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
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
    val result =  angle2-angle1
    return normalizeAngle(result)
  }
  fun verticalDifference(orientation: Orientation): Double {
    var angle1 = normalizeAngleToPositive(verticalAngle)
    var angle2 = normalizeAngleToPositive(orientation.verticalAngle)
    val result =  angle2-angle1
    return normalizeAngle(result)
  }
  fun difference(orientation: Orientation): Orientation {
    return Orientation(this.verticalDifference(orientation), this.horizontalDifference(orientation))
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
    fun angleBetween(pos1: BlockPos, pos2: BlockPos): Orientation {
      val newPos = pos2.subtract(pos1)
      val x = newPos.getX().toDouble()
      val y = newPos.getY().toDouble()
      val z = newPos.getZ().toDouble()
      val zeroAngle = PI/2
      var horizontalAngle = atan2(z, x)-zeroAngle
      horizontalAngle = horizontalAngle/PI*180
      val horizontalDistance = sqrt(x.pow(2)+z.pow(2))
      var verticalAngle = atan2(y, horizontalDistance)
      verticalAngle = verticalAngle/PI*180
      return Orientation(normalizeAngle(verticalAngle), normalizeAngle(horizontalAngle))
    }
  }
}
