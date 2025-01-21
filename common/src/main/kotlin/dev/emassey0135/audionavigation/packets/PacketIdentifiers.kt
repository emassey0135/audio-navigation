package dev.emassey0135.audionavigation.packets

import net.minecraft.util.Identifier
import dev.emassey0135.audionavigation.AudioNavigation

object PacketIdentifiers {
  @JvmField val POI_REQUEST_ID = Identifier.of(AudioNavigation.MOD_ID, "poi_request")
  @JvmField val POI_LIST_ID = Identifier.of(AudioNavigation.MOD_ID, "poi_list")
  @JvmField val ADD_LANDMARK_ID = Identifier.of(AudioNavigation.MOD_ID, "add_landmark")
  @JvmField val DELETE_LANDMARK_ID = Identifier.of(AudioNavigation.MOD_ID, "delete_landmark")
}
