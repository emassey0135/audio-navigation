package dev.emassey0135.audionavigation.client.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import net.minecraft.client.resources.language.I18n
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import dev.emassey0135.audionavigation.AudioNavigation

class Orientation(val verticalAngle: Double, val horizontalAngle: Double) {
  constructor (angles: Vec2): this(normalizeAngle(-angles.x.toDouble()), normalizeAngle(angles.y.toDouble()))
  enum class ClockHand {
    TWELVE, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN;
    fun translate(): String {
      return I18n.get("${AudioNavigation.MOD_ID}.clock_hands.${this.toString().lowercase()}")
    }
  }
  enum class Direction {
    IN_FRONT, RIGHT, BEHIND, LEFT;
    fun translate(): String {
      return I18n.get("${AudioNavigation.MOD_ID}.directions.${this.toString().lowercase()}")
    }
  }
  enum class CompassDirection {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
    fun translate(): String {
      return I18n.get("${AudioNavigation.MOD_ID}.compass_directions.${this.toString().lowercase()}")
    }
  }
  enum class VerticalDirection {
    STRAIGHT, UP, DOWN;
    fun translate(): String {
      return I18n.get("${AudioNavigation.MOD_ID}.vertical_directions.${this.toString().lowercase()}")
    }
  }
  enum class HorizontalDirectionType {
    CLOCK_HAND, DIRECTION_AND_ANGLE, DIRECTION, ANGLE, COMPASS_DIRECTION
  }
  enum class VerticalDirectionType {
    DIRECTION_AND_ANGLE, DIRECTION, ANGLE, ABSOLUTE_DIRECTION_AND_ANGLE, ABSOLUTE_DIRECTION, ABSOLUTE_ANGLE
  }
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
  fun toSpeakableString(reference: Orientation, includeVerticalDirection: Boolean, horizontalDirectionType: HorizontalDirectionType, verticalDirectionType: VerticalDirectionType): String {
    val difference = reference.difference(this)
    val horizontalDirection = when (horizontalDirectionType) {
      HorizontalDirectionType.CLOCK_HAND -> angleToClockHand(difference.horizontalAngle).translate()
      HorizontalDirectionType.DIRECTION_AND_ANGLE -> I18n.get("${AudioNavigation.MOD_ID}.angle.angle_with_direction", angleToSpeakableString(difference.horizontalAngle), angleToDirection(difference.horizontalAngle).translate())
      HorizontalDirectionType.DIRECTION -> angleToDirection(difference.horizontalAngle).translate()
      HorizontalDirectionType.ANGLE -> angleToSpeakableString(difference.horizontalAngle)
      HorizontalDirectionType.COMPASS_DIRECTION -> angleToCompassDirection(horizontalAngle).translate()
    }
    if (!includeVerticalDirection)
      return horizontalDirection
    val verticalDirection = when (verticalDirectionType) {
      VerticalDirectionType.DIRECTION_AND_ANGLE -> I18n.get("${AudioNavigation.MOD_ID}.angle.angle_with_direction", angleToSpeakableString(difference.verticalAngle), angleToDirection(difference.verticalAngle).translate())
      VerticalDirectionType.DIRECTION -> angleToDirection(difference.verticalAngle).translate()
      VerticalDirectionType.ANGLE -> angleToSpeakableString(difference.verticalAngle)
      VerticalDirectionType.ABSOLUTE_DIRECTION_AND_ANGLE -> I18n.get("${AudioNavigation.MOD_ID}.angle.angle_with_direction", angleToSpeakableString(verticalAngle), angleToDirection(verticalAngle).translate())
      VerticalDirectionType.ABSOLUTE_DIRECTION -> angleToDirection(verticalAngle).translate()
      VerticalDirectionType.ABSOLUTE_ANGLE -> angleToSpeakableString(verticalAngle)
    }
    return "$horizontalDirection, $verticalDirection"
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
    private fun angleToClockHand(angle: Double): ClockHand {
      var result = (normalizeAngleToPositive(angle)/30).roundToInt()
      result = if (result==0) 12 else result
      return ClockHand.entries.get(result)
    }
    private fun angleToDirection(angle: Double): Direction {
      var result = (normalizeAngleToPositive(angle)/90).roundToInt()
      result = if (result==4) 0 else result
      return Direction.entries.get(result)
    }
    private fun angleToCompassDirection(angle: Double): CompassDirection {
      var result = (normalizeAngleToPositive(angle)/45).roundToInt()
      result = if (result==8) 0 else result
      return CompassDirection.entries.get(result)
    }
    private fun angleToVerticalDirection(angle: Double): VerticalDirection {
      val angle = normalizeAngle(angle)
      return when {
        angle==0.0 -> VerticalDirection.STRAIGHT
        angle>0.0 -> VerticalDirection.UP
        else -> VerticalDirection.DOWN
      }
    }
    private fun angleToSpeakableString(angle: Double): String {
      return I18n.get("${AudioNavigation.MOD_ID}.angle.degrees", angle.roundToInt())
    }
  }
}
