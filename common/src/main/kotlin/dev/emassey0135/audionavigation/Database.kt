package dev.emassey0135.audionavigation

import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.pow
import kotlin.math.sqrt
import org.sqlite.Function
import org.sqlite.SQLiteConfig
import dev.emassey0135.audionavigation.AudioNavigation

object Database {
  @JvmField val queryConnection: Connection
  @JvmField val updateConnection: Connection
  class DistanceFunction(): Function() {
    override protected fun xFunc() {
      if (args()!=6)
        error("Invalid number of arguments passed to distance function")
      result(sqrt((value_double(0)-value_double(3)).pow(2.0) + (value_double(1)-value_double(4)).pow(2.0) + (value_double(2)-value_double(5)).pow(2.0)))
    }
  }
  init {
    val config = SQLiteConfig()
    config.enableLoadExtension(true)
    updateConnection = DriverManager.getConnection("jdbc:sqlite:poi.db", config.toProperties())
    queryConnection = DriverManager.getConnection("jdbc:sqlite:poi.db", config.toProperties())
  }
  fun initialize() {
    updateConnection.createStatement().use {
      it.execute("CREATE VIRTUAL TABLE IF NOT EXISTS features USING RTREE(id, minX, maxX, minY, maxY, minZ, maxZ, +name TEXT, +x REAL, +y REAL, +z REAL)")
    }
    Function.create(queryConnection, "distance", DistanceFunction(), 6, Function.FLAG_DETERMINISTIC)
    AudioNavigation.logger.info("Database initialized.")
  }
}
