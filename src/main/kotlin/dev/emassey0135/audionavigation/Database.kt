package dev.emassey0135.audionavigation

import java.sql.Connection
import java.sql.DriverManager
import org.sqlite.SQLiteConfig

object Database {
  @JvmField var connection: Connection
  init {
    val config = SQLiteConfig()
    config.enableLoadExtension(true)
    connection = DriverManager.getConnection("jdbc:sqlite:poi.db", config.toProperties())
    val statement = connection.createStatement()
    statement.execute("SELECT load_extension('/usr/lib/mod_spatialite.so')")
    statement.execute("SELECT InitSpatialMetadata(1)")
    if (!statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='features'").next()) {
      statement.execute("CREATE TABLE IF NOT EXISTS features (id INTEGER NOT NULL AUTOINCREMENT PRIMARY KEY, name TEXT NOT NULL)")
      statement.execute("SELECT AddGeometryColumn('features', 'location', -1, 'POINTZ', 'XYZ')")
      statement.execute("SELECT CreateSpatialIndex('features', 'location')")
    }
  }
}
