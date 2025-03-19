package dev.emassey0135.audionavigation.packets

import net.minecraft.resources.ResourceLocation
import dev.emassey0135.audionavigation.AudioNavigation

object PacketIdentifiers {
  @JvmField val POI_REQUEST_ID = ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "poi_request")
  @JvmField val POI_LIST_ID = ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "poi_list")
  @JvmField val ADD_LANDMARK_ID = ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "add_landmark")
  @JvmField val DELETE_LANDMARK_ID = ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "delete_landmark")
}
