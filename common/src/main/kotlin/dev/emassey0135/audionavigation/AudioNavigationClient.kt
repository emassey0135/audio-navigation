package dev.emassey0135.audionavigation

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.Interval
import dev.emassey0135.audionavigation.MainMenuScreen
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.Speech

object AudioNavigationClient {
  private fun speakPoi(origin: BlockPos, orientation: Direction, poi: Poi, distance: Double) {
    val text = if (Configs.clientConfig.announcements.detailedAnnouncements.get())
      I18n.translate("${AudioNavigation.MOD_ID}.poi_announcement_detailed", poi.name, distance.toInt())
      else
      I18n.translate("${AudioNavigation.MOD_ID}.poi_announcement", poi.name)
    Speech.speakText(text, origin, orientation, poi.pos)
  }
  private val poiListQueue = ArrayBlockingQueue<PoiList>(16)
  private var oldPoiList = PoiList(listOf())
  private var mutex = ReentrantLock()
  private fun waitForAndSpeakPoiList() {
    mutex.lock()
    val poiList = poiListQueue.take()
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player==null) return
    if (poiList!=oldPoiList) {
      val newPoiList = poiList.subtract(oldPoiList)
      oldPoiList = poiList
      val origin = BlockPos.ofFloored(player.getPos())
      val orientation = player.getFacing()
      newPoiList.toList().forEach { poi -> speakPoi(origin, orientation, poi.poi, poi.distance) }
    }
    mutex.unlock()
  }
  private fun waitForAndSpeakCompletePoiList() {
    mutex.lock()
    val poiList = poiListQueue.take()
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player==null) return
    val origin = BlockPos.ofFloored(player.getPos())
    val orientation = player.getFacing()
    poiList.toList().forEach { poi -> speakPoi(origin, orientation, poi.poi, poi.distance) }
    mutex.unlock()
  }
  @JvmStatic @ExpectPlatform fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    error("This function is not implemented.")
  }
  @JvmStatic @ExpectPlatform fun sendAddLandmark(addLandmarkPayload: AddLandmarkPayload) {
    error("This function is not implemented.")
  }
  fun handlePoiList(payload: PoiListPayload) {
    thread { poiListQueue.put(payload.poiList) }
  }
  fun announceNearbyPois() {
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player!=null) {
      sendPoiRequest(PoiRequestPayload(BlockPos.ofFloored(player.getPos()), Configs.clientConfig.manualAnnouncements.announcementRadius.get().toDouble(), Configs.clientConfig.manualAnnouncements.maxAnnouncements.get(), Configs.clientConfig.manualAnnouncements.enableVerticalLimit.get(), Configs.clientConfig.manualAnnouncements.verticalLimit.get().toDouble()))
      thread { waitForAndSpeakCompletePoiList() }
    }
  }
  fun addLandmark(name: String) {
    val minecraftClient = MinecraftClient.getInstance()
    val player = minecraftClient.player
    if (player!=null)
      sendAddLandmark(AddLandmarkPayload(name, BlockPos.ofFloored(player.getPos())))
  }
  private val interval = Interval.sec(5)
  private val OPEN_MAIN_MENU_KEYBINDING = KeyBinding("key.${AudioNavigation.MOD_ID}.open_main_menu", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F6, "category.${AudioNavigation.MOD_ID}")
  private val SPEAK_NEARBY_POIS_KEYBINDING = KeyBinding("key.${AudioNavigation.MOD_ID}.speak_nearby_pois", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F7, "category.${AudioNavigation.MOD_ID}")
  fun initialize() {
    Speech.initialize()
    KeyMappingRegistry.register(OPEN_MAIN_MENU_KEYBINDING)
    KeyMappingRegistry.register(SPEAK_NEARBY_POIS_KEYBINDING)
    interval.beReady()
    AudioNavigation.logger.info("The mod has been initialized.")
    val minecraftClient = MinecraftClient.getInstance()
    ClientTickEvent.CLIENT_LEVEL_PRE.register { world ->
      while (OPEN_MAIN_MENU_KEYBINDING.wasPressed())
        minecraftClient.setScreen(MainMenuScreen())
      while (SPEAK_NEARBY_POIS_KEYBINDING.wasPressed())
        announceNearbyPois()
      if (interval.isReady()) {
        val player = minecraftClient.player
        if (player!=null) {
          sendPoiRequest(PoiRequestPayload(BlockPos.ofFloored(player.getPos()), Configs.clientConfig.announcements.announcementRadius.get().toDouble(), Configs.clientConfig.announcements.maxAnnouncements.get(), Configs.clientConfig.announcements.enableVerticalLimit.get(), Configs.clientConfig.announcements.verticalLimit.get().toDouble()))
          thread { waitForAndSpeakPoiList() }
        }
      }
    }
  }
}
