package dev.emassey0135.audionavigation.poi

import java.sql.PreparedStatement
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.pow
import kotlin.math.sqrt
import net.minecraft.client.resource.language.I18n
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.Uuids
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.util.Database

enum class PoiType {
  LANDMARK, FEATURE, STRUCTURE;
  companion object {
    @JvmField val PACKET_CODEC = PacketCodecs.indexed({ n -> PoiType.entries.get(n) }, { poiType -> poiType.ordinal })
  }
}

data class Poi(val type: PoiType, val name: String, val pos: BlockPos) {
  fun distance(pos2: BlockPos): Double {
    val x = pos.getX().toDouble()
    val x2 = pos2.getX().toDouble()
    val y = pos.getY().toDouble()
    val y2 = pos2.getY().toDouble()
    val z = pos.getZ().toDouble()
    val z2 = pos2.getZ().toDouble()
    return sqrt((x-x2).pow(2)+(y-y2).pow(2)+(z-z2).pow(2))
  }
  fun distance(poi: Poi): Double {
    return distance(poi.pos)
  }
  fun addToDatabase(world: ServerWorld) {
    addToDatabaseMutex.lock()
    if (addToDatabaseStatement == null)
      addToDatabaseStatement = Database.connection.prepareStatement("INSERT INTO pois (id, minX, maxX, minY, maxY, minZ, maxZ, world, type, name, x, y, z) VALUES(NULL, ?1, ?1, ?2, ?2, ?3, ?3, ?6, ?4, ?5, ?1, ?2, ?3)")
    addToDatabaseStatement?.setInt(1, pos.getX())
    addToDatabaseStatement?.setInt(2, pos.getY())
    addToDatabaseStatement?.setInt(3, pos.getZ())
    addToDatabaseStatement?.setInt(4, type.ordinal)
    addToDatabaseStatement?.setString(5, name)
    addToDatabaseStatement?.setBytes(6, Uuids.toByteArray(AudioNavigation.getWorldUUID(world)))
    addToDatabaseStatement?.executeUpdate()
    addToDatabaseMutex.unlock()
    Database.scheduleCommitIfNeeded()
  }
  fun positionAsString(): String {
    return "(${pos.getX().toString()}, ${pos.getY().toString()}, ${pos.getZ().toString()})"
  }
  fun positionAsNarratableString(): String {
    val x = pos.getX()
    val xString = if (x<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -x) else x.toString()
    val y = pos.getY()
    val yString = if (y<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -y) else y.toString()
    val z = pos.getZ()
    val zString = if (z<0) I18n.translate("${AudioNavigation.MOD_ID}.number.negative", -z) else z.toString()
    return "($xString, $yString, $zString)"
  }
  companion object {
    private var addToDatabaseStatement: PreparedStatement? = null
    private val addToDatabaseMutex = ReentrantLock()
    private var deleteLandmarkStatement: PreparedStatement? = null
    private val deleteLandmarkMutex = ReentrantLock()
    fun deleteLandmark(id: Int) {
      deleteLandmarkMutex.lock()
      if (deleteLandmarkStatement==null)
        deleteLandmarkStatement = Database.connection.prepareStatement("DELETE FROM pois WHERE id = ?1")
      deleteLandmarkStatement?.setInt(1, id)
      deleteLandmarkStatement?.executeUpdate()
      deleteLandmarkMutex.unlock()
      Database.scheduleCommitIfNeeded()
    }
    @JvmField val PACKET_CODEC = PacketCodec.tuple(
      PoiType.PACKET_CODEC, Poi::type,
      PacketCodecs.STRING, Poi::name,
      BlockPos.PACKET_CODEC, Poi::pos,
      ::Poi)
  }
}
