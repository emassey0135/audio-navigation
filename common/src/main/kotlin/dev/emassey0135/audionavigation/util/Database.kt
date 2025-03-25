package dev.emassey0135.audionavigation.util

import java.lang.Thread
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Optional
import kotlin.concurrent.thread
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.sqlite.Function
import org.sqlite.SQLiteConfig
import net.minecraft.core.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiData
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiType

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
    @OptIn(ExperimentalSerializationApi::class)
    override protected fun xFunc() {
      if (args()!=6)
        error("Invalid number of arguments passed to filterPoi function")
      val type = PoiType.entries.get(value_int(0))
      val name = value_text(1)
      val x = value_double(2).toInt()
      val y = value_double(3).toInt()
      val z = value_double(4).toInt()
      val dataBinary = value_blob(5)
      var data: Optional<PoiData> = Optional.empty()
      if (dataBinary!=null)
        data = Optional.of(ProtoBuf.decodeFromByteArray(dataBinary))
      result(PoiList.filterPoi(Poi(type, name, BlockPos(x, y, z), data)))
    }
  }
  fun initialize() {
    connection.createStatement().use {
      it.execute("CREATE VIRTUAL TABLE IF NOT EXISTS pois2 USING RTREE_I32(id, minX, maxX, minY, maxY, minZ, maxZ, +world BLOB, +type INTEGER, +name TEXT, +data BLOB, +x INTEGER, +y INTEGER, +z INTEGER)")
      if (it.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'pois'").use { it.next() }) {
        AudioNavigation.logger.info("Migrating database to schema version 2. This may take a few minutes.")
        it.executeUpdate("INSERT INTO pois2 (id, minX, maxX, minY, maxY, minZ, maxZ, world, type, name, data, x, y, z) SELECT id, minX, maxX, minY, maxY, minZ, maxZ, world, type, name, NULL, x, y, z FROM pois")
        it.execute("DROP TABLE pois")
        it.execute("VACUUM")
      }
    }
    Function.create(connection, "filterPoi", FilterPoiFunction(), 6, Function.FLAG_DETERMINISTIC)
    connection.setAutoCommit(false)
    AudioNavigation.logger.info("Database initialized.")
  }
}
