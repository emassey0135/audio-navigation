package dev.emassey0135.audionavigation

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sin
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

class Orientation(val angles: Vec2f) {
  constructor (x: Float, y: Float): this(Vec2f(x, y))
  fun verticalAngle(): Float {
    return angles.x
  }
  fun horizontalAngle(): Float {
    return angles.y
  }
  fun toVector(): Vec3d {
    val verticalAngle = angles.x/180*PI
    val horizontalAngle = angles.y/180*PI
    val x = sin(horizontalAngle)
    val y = -sin(horizontalAngle)
    val z = cos(horizontalAngle)
    return Vec3d(x, y, z)
  }
  companion object {
    fun horizontalAngleBetween(pos1: BlockPos, pos2: BlockPos): Orientation {
      val newPos = pos2.subtract(pos1)
      val zeroAngle = atan2(1.0, 0.0)
      val angleInRadians = atan2(newPos.getY().toDouble(), newPos.getX().toDouble())-zeroAngle
      val angleInDegrees = angleInRadians/PI*180
      return Orientation(0f, angleInDegrees.toFloat())
    }
  }
}
