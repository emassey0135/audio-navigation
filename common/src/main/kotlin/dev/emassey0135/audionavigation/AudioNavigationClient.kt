package dev.emassey0135.audionavigation

import java.util.UUID
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.MinecraftClient
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Beacon
import dev.emassey0135.audionavigation.ClientConfig
import dev.emassey0135.audionavigation.Interval
import dev.emassey0135.audionavigation.Library
import dev.emassey0135.audionavigation.MainMenuScreen
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.PoiAnnouncements
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.Speech

object AudioNavigationClient {
  @JvmStatic @ExpectPlatform fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    error("This function is not implemented.")
  }
  @JvmStatic @ExpectPlatform fun sendAddLandmark(addLandmarkPayload: AddLandmarkPayload) {
    error("This function is not implemented.")
  }
  @JvmStatic @ExpectPlatform fun sendDeleteLandmark(deleteLandmarkPayload: DeleteLandmarkPayload) {
    error("This function is not implemented.")
  }
  val poiListHandlers = HashMap<UUID, (PoiListPayload) -> Unit>()
  fun registerPoiListHandler(requestID: UUID, handler: (PoiListPayload) -> Unit) {
    poiListHandlers.put(requestID, handler)
  }
  fun handlePoiList(payload: PoiListPayload) {
    if (poiListHandlers.containsKey(payload.requestID)) {
      val handler = poiListHandlers.get(payload.requestID)
      poiListHandlers.remove(payload.requestID)
      handler!!(payload)
    }
  }
  private val interval = Interval.sec(5)
  private val OPEN_MAIN_MENU_KEYBINDING = KeyBinding("key.${AudioNavigation.MOD_ID}.open_main_menu", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F6, "category.${AudioNavigation.MOD_ID}")
  private val ANNOUNCE_NEARBY_POIS_KEYBINDING = KeyBinding("key.${AudioNavigation.MOD_ID}.announce_nearby_pois", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F7, "category.${AudioNavigation.MOD_ID}")
  fun initialize() {
    Library.initialize()
    KeyMappingRegistry.register(OPEN_MAIN_MENU_KEYBINDING)
    KeyMappingRegistry.register(ANNOUNCE_NEARBY_POIS_KEYBINDING)
    interval.beReady()
    val minecraftClient = MinecraftClient.getInstance()
    ClientLifecycleEvent.CLIENT_STARTED.register { client ->
      SoundPlayer.initialize()
      Speech.initialize()
      ClientConfig.initialize()
      Speech.configure()
      Beacon.initialize()
      AudioNavigation.logger.info("Audio Navigation client has been initialized.")
    }
    ClientTickEvent.CLIENT_LEVEL_PRE.register { world ->
      while (OPEN_MAIN_MENU_KEYBINDING.wasPressed())
        minecraftClient.setScreen(MainMenuScreen())
      while (ANNOUNCE_NEARBY_POIS_KEYBINDING.wasPressed())
        PoiAnnouncements.triggerManualAnnouncements()
      if (interval.isReady()) {
        PoiAnnouncements.triggerAutomaticAnnouncements()
      }
    }
  }
}
