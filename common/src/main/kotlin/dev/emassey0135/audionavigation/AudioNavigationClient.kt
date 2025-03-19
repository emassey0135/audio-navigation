package dev.emassey0135.audionavigation

import java.util.UUID
import com.mojang.blaze3d.platform.InputConstants
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import dev.emassey0135.audionavigation.config.ClientConfig
import dev.emassey0135.audionavigation.features.Beacon
import dev.emassey0135.audionavigation.features.PoiAnnouncements
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.screens.MainMenuScreen
import dev.emassey0135.audionavigation.sound.SoundPlayer
import dev.emassey0135.audionavigation.speech.Speech
import dev.emassey0135.audionavigation.util.Interval
import dev.emassey0135.audionavigation.util.Library

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
  private val OPEN_MAIN_MENU_KEYMAPPING = KeyMapping("key.${AudioNavigation.MOD_ID}.open_main_menu", InputConstants.Type.KEYSYM, InputConstants.KEY_F6, "category.${AudioNavigation.MOD_ID}")
  private val ANNOUNCE_NEARBY_POIS_KEYMAPPING = KeyMapping("key.${AudioNavigation.MOD_ID}.announce_nearby_pois", InputConstants.Type.KEYSYM, InputConstants.KEY_F7, "category.${AudioNavigation.MOD_ID}")
  fun initialize() {
    Library.initialize()
    KeyMappingRegistry.register(OPEN_MAIN_MENU_KEYMAPPING)
    KeyMappingRegistry.register(ANNOUNCE_NEARBY_POIS_KEYMAPPING)
    interval.beReady()
    val minecraftClient = Minecraft.getInstance()
    ClientLifecycleEvent.CLIENT_STARTED.register { client ->
      SoundPlayer.initialize()
      Speech.initialize()
      ClientConfig.initialize()
      Speech.configure()
      Beacon.initialize()
      AudioNavigation.logger.info("Audio Navigation client has been initialized.")
    }
    ClientTickEvent.CLIENT_LEVEL_PRE.register { world ->
      while (OPEN_MAIN_MENU_KEYMAPPING.consumeClick())
        minecraftClient.setScreen(MainMenuScreen())
      while (ANNOUNCE_NEARBY_POIS_KEYMAPPING.consumeClick())
        PoiAnnouncements.triggerManualAnnouncements()
      if (interval.isReady()) {
        PoiAnnouncements.triggerAutomaticAnnouncements()
      }
    }
  }
}
