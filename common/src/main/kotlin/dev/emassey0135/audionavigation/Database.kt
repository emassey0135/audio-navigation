package dev.emassey0135.audionavigation

import java.lang.Thread
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import org.sqlite.Function
import org.sqlite.SQLiteConfig
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.PoiType

object Database {
  @JvmField val connection = DriverManager.getConnection("jdbc:sqlite:poi.db")
  private var commitScheduled = AtomicBoolean()
  fun scheduleCommitIfNeeded() {
    if (commitScheduled.compareAndSet(false, true))
      thread {
        Thread.sleep(1000)
        commitScheduled.set(false)
        connection.commit()
      }
  }
  class FilterPoiFunction(): Function() {
    override protected fun xFunc() {
      if (args()!=5)
        error("Invalid number of arguments passed to distance function")
      val type = PoiType.entries.get(value_int(0))
      val name = value_text(1)
      val x = value_double(2).toInt()
      val y = value_double(3).toInt()
      val z = value_double(4).toInt()
      result(PoiList.filterPoi(Poi(type, name, BlockPos(x, y, z))))
    }
  }
  fun initialize() {
    connection.createStatement().use {
      it.execute("CREATE VIRTUAL TABLE IF NOT EXISTS pois USING RTREE(id, minX, maxX, minY, maxY, minZ, maxZ, +world BLOB, +type INTEGER, +name TEXT, +x INTEGER, +y INTEGER, +z INTEGER)")
    }
    Function.create(connection, "filterPoi", FilterPoiFunction(), 5, Function.FLAG_DETERMINISTIC)
    connection.setAutoCommit(false)
    AudioNavigation.logger.info("Database initialized.")
  }
}
