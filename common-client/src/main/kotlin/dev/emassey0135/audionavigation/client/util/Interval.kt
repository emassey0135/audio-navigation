package dev.emassey0135.audionavigation.client.util

/* This code has been derived from Minecraft Access (https://github.com/minecraft-access/minecraft-access).
   The project is released under the GPL-3.0; see LICENSE.minecraft-access
*/

class Interval private constructor(private var lastRunTime: Long, private var delay: Long) {
  enum class Unit(private val factor: Long) {
    Millisecond(1_000_000),
    Second(1_000_000_000);
    fun toNano(value: Long): Long = value * factor
  }

  fun reset() {
    lastRunTime = System.nanoTime()
  }

  fun beReady() {
    lastRunTime = 0
  }

  // delay == 0 disables the timer; auto-resets when ready
  fun isReady(): Boolean {
    if (delay == 0L) return false
    if (System.nanoTime() - lastRunTime > delay) {
      reset()
      return true
    }
    return false
  }

  fun setDelay(delay: Long, unit: Unit) {
    this.delay = unit.toNano(delay)
  }

  fun adjustNextReadyTimeBy(anyFunctionTriggered: Boolean) {
    if (anyFunctionTriggered) reset() else beReady()
  }

  companion object {
    fun ms(delay: Long): Interval = Interval(System.nanoTime(), Unit.Millisecond.toNano(delay))
    fun ms(delay: Int): Interval = ms(delay.toLong())
    fun sec(delay: Long): Interval = Interval(System.nanoTime(), Unit.Second.toNano(delay))
    fun sec(delay: Int): Interval = sec(delay.toLong())
  }
}
