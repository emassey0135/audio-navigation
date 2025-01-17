package dev.emassey0135.audionavigation.packets

import net.minecraft.util.Identifier

object PacketIdentifiers {
  @JvmField val POI_REQUEST_ID = Identifier.of("audionavigation", "poi_request")
  @JvmField val POI_LIST_ID = Identifier.of("audionavigation", "poi_list")
  @JvmField val ADD_LANDMARK_ID = Identifier.of("audionavigation", "add_landmark")
}
