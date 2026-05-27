package dev.emassey0135.audionavigation.poi

import java.util.Optional
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import dev.emassey0135.audionavigation.AudioNavigation

object Landmarks {
  fun addLandmark(world: ServerLevel, player: ServerPlayer, name: String, pos: BlockPos, visibleToOtherPlayers: Boolean) {
    val data = PoiData(player.getUUID(), visibleToOtherPlayers)
    Poi(PoiType.LANDMARK, name, pos, Optional.of(data)).addToDatabase(world)
  }
  fun deleteLandmark(id: Int) {
    Poi.deleteLandmark(id)
  }
  fun addLandmarkOnDeath(player: ServerPlayer) {
    val config = AudioNavigation.config!!
    if (config.saveLandmarksOnPlayerDeath)
      addLandmark(player.level(), player, "${player.getName().getString()}'s death location", player.blockPosition(), config.deathLandmarksVisibleToOtherPlayers)
  }
}
