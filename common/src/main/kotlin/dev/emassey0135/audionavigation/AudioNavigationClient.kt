package dev.emassey0135.audionavigation

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.SynchronousQueue
import kotlin.concurrent.thread
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.Interval
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.Speech

object AudioNavigationClient {
  private fun speakPoi(origin: BlockPos, orientation: Direction, poi: Poi) {
    Speech.speakText(poi.identifier.getPath(), origin, orientation, poi.pos)
  }
  private val poiListQueue = SynchronousQueue<PoiList>()
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
      newPoiList.toList().forEach { poi -> speakPoi(origin, orientation, poi) }
    }
    mutex.unlock()
  }
  @JvmStatic @ExpectPlatform fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    error("This function is not implemented.")
  }
  fun handlePoiList(payload: PoiListPayload) {
    thread { poiListQueue.put(payload.poiList) }
  }
  private val interval = Interval.sec(5)
  private val OPEN_CONFIG_SCREEN_KEYBINDING = KeyBinding("key.${AudioNavigation.MOD_ID}.open_config_screen", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F6, "category.${AudioNavigation.MOD_ID}")
  fun initialize() {
    Speech.initialize()
    KeyMappingRegistry.register(OPEN_CONFIG_SCREEN_KEYBINDING)
    interval.beReady()
    AudioNavigation.logger.info("The mod has been initialized.")
    val minecraftClient = MinecraftClient.getInstance()
    ClientTickEvent.CLIENT_LEVEL_PRE.register { world ->
      while (OPEN_CONFIG_SCREEN_KEYBINDING.wasPressed())
        ConfigApi.openScreen(AudioNavigation.MOD_ID)
      if (interval.isReady()) {
        val player = minecraftClient.player
        if (player!=null) {
          sendPoiRequest(PoiRequestPayload(BlockPos.ofFloored(player.getPos()), Configs.clientConfig.announcementRadius.get().toDouble(), Configs.clientConfig.maxAnnouncements.get()))
          thread { waitForAndSpeakPoiList() }
        }
      }
    }
  }
}
