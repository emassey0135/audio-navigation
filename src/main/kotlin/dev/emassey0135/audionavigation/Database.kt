package dev.emassey0135.audionavigation

import java.sql.Connection
import java.sql.DriverManager
import org.sqlite.SQLiteConfig

object Database {
  var connection: Connection
  init {
    val config = SQLiteConfig()
    config.enableLoadExtension(true)
    connection = DriverManager.getConnection("jdbc:sqlite:poi.db", config.toProperties())
    val statement = connection.createStatement()
    statement.execute("SELECT load_extension('/usr/lib/mod_spatialite.so')")
    statement.execute("SELECT InitSpatialMetadata(1)")
  }
}
