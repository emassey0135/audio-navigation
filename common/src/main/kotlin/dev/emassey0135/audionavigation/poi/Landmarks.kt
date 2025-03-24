package dev.emassey0135.audionavigation.poi

import java.util.Optional
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object Landmarks {
  fun addLandmark(world: ServerLevel, player: ServerPlayer, name: String, pos: BlockPos, visibleToOtherPlayers: Boolean) {
    val data = PoiData(player.getUUID(), visibleToOtherPlayers)
    Poi(PoiType.LANDMARK, name, pos, Optional.of(data)).addToDatabase(world)
  }
  fun deleteLandmark(id: Int) {
    Poi.deleteLandmark(id)
  }
}
