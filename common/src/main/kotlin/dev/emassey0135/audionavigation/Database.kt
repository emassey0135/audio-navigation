package dev.emassey0135.audionavigation

import java.lang.Thread
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt
import org.sqlite.Function
import org.sqlite.SQLiteConfig
import dev.emassey0135.audionavigation.AudioNavigation

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
  class DistanceFunction(): Function() {
    override protected fun xFunc() {
      if (args()!=6)
        error("Invalid number of arguments passed to distance function")
      result(sqrt((value_double(0)-value_double(3)).pow(2.0) + (value_double(1)-value_double(4)).pow(2.0) + (value_double(2)-value_double(5)).pow(2.0)))
    }
  }
  fun initialize() {
    connection.createStatement().use {
      it.execute("CREATE VIRTUAL TABLE IF NOT EXISTS pois USING RTREE(id, minX, maxX, minY, maxY, minZ, maxZ, +world BLOB, +type INTEGER, +name TEXT, +x REAL, +y REAL, +z REAL)")
    }
    Function.create(connection, "distance", DistanceFunction(), 6, Function.FLAG_DETERMINISTIC)
    connection.setAutoCommit(false)
    AudioNavigation.logger.info("Database initialized.")
  }
}
