package dev.emassey0135.audionavigation.screens

import java.util.concurrent.SynchronousQueue
import java.util.Optional
import java.util.UUID
import kotlin.concurrent.thread
import kotlin.math.pow
import net.minecraft.core.BlockPos
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.AlertScreen
import net.minecraft.client.gui.screens.ConfirmScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.features.Beacon
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiListItem
import dev.emassey0135.audionavigation.poi.PoiList
import dev.emassey0135.audionavigation.poi.PoiRequest
import dev.emassey0135.audionavigation.poi.PoiType

class LandmarkListScreen(val parent: Screen, val minecraftClient: Minecraft, val poiList: PoiList, val startingRadius: Int): Screen(Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list")) {
  private class LandmarkEntry(val font: Font, val poi: PoiListItem): ObjectSelectionList.Entry<LandmarkEntry>() {
    override fun getNarration(): Component {
      return Component.literal("${poi.poi.name}, ${I18n.get("${AudioNavigation.MOD_ID}.poi_distance", poi.distance.toInt())}, ${poi.poi.positionAsNarratableString()}")
    }
    override fun render(context: GuiGraphics, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
      val landmarkNameText = StringWidget(x, y, entryWidth, 20, Component.literal(poi.poi.name), font)
      val landmarkDistanceText = StringWidget(x, y+30, entryWidth, 20, Component.translatable("${AudioNavigation.MOD_ID}.poi_distance", poi.distance.toInt()), font)
      val landmarkPositionText = StringWidget(x, y+60, entryWidth, 20, Component.literal(poi.poi.positionAsString()), font)
      landmarkNameText.renderWidget(context, mouseX, mouseY, tickDelta)
      landmarkDistanceText.renderWidget(context, mouseX, mouseY, tickDelta)
      landmarkPositionText.renderWidget(context, mouseX, mouseY, tickDelta)
    }
  }
  private class LandmarkList(minecraftClient: Minecraft, x: Int, y: Int, width: Int, height: Int, val font: Font, val poiList: PoiList): ObjectSelectionList<LandmarkEntry>(minecraftClient, width, height, y, 90) {
    init {
      setX(x)
      poiList.toList().forEach { poi -> addEntry(LandmarkEntry(font, poi)) }
    }
  }
  private var landmarkList: LandmarkList? = null
  fun startBeacon() {
    val selectedEntry = landmarkList!!.getSelected()
    if (selectedEntry==null) {
      minecraftClient.setScreen(AlertScreen(
        { minecraftClient.setScreen(this) },
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.notice_none_selected.title"),
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.notice_none_selected.message")))
    }
    else {
      Beacon.startBeacon(selectedEntry.poi.poi)
      onClose()
    }
  }
  fun delete() {
    val selectedEntry = landmarkList!!.getSelected()
    if (selectedEntry==null)
      minecraftClient.setScreen(AlertScreen(
        { minecraftClient.setScreen(this) },
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.notice_none_selected.title"),
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.notice_none_selected.message")))
    else
      minecraftClient.setScreen(ConfirmScreen(
        { choice ->
          if (choice) {
            AudioNavigationClient.sendDeleteLandmark(DeleteLandmarkPayload(selectedEntry.poi.id))
            minecraftClient.setScreen(LandmarkListScreen(parent, minecraftClient, poiList.also { it.delete(selectedEntry.poi.id) }, startingRadius))
          }
          else {
            minecraftClient.setScreen(this)
          }
        },
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.delete_confirm.title"),
        Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.delete_confirm.message", selectedEntry.poi.poi.name)))
  }
  fun goUp() {
    Minecraft.getInstance()?.setScreen(parent)
  }
  override fun init() {
    landmarkList = LandmarkList(minecraftClient, 10, 10, width/2-20, height-20, font, poiList)
    addRenderableWidget(landmarkList)
    addRenderableWidget(CycleButton.builder<Int>({ value -> Component.literal(value.toString()) })
      .withValues((6..26).map { exponent -> (2.0).pow(exponent).toInt() })
      .withInitialValue(startingRadius)
      .create(width/2+10, 50, 300, 20, Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.radius_button"), { widget, radius ->
        onClose()
        openLandmarkListScreen(parent, radius)
      }))
    addRenderableWidget(Button.builder(Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.start_beacon_button"), { button -> startBeacon() })
      .bounds(width/2+10, 110, 100, 20)
      .build())
    addRenderableWidget(Button.builder(Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.delete_button"), { button -> delete() })
      .bounds(width/2+10, 170, 100, 20)
      .build())
    addRenderableWidget(Button.builder(Component.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.back_button"), { button -> goUp() })
      .bounds(width/2+10, 230, 100, 20)
      .build())
  }
  companion object {
    fun openLandmarkListScreen(parent: Screen, startingRadius: Int) {
      thread(block = fun(): Unit {
        val minecraftClient = Minecraft.getInstance()
        val player = minecraftClient.player
        if (player==null)
          return
        val origin = BlockPos.containing(player.position())
        val poiListQueue = SynchronousQueue<PoiList>()
        val requestID = UUID.randomUUID()
        AudioNavigationClient.registerPoiListHandler(requestID, { payload -> poiListQueue.put(payload.poiList) })
        AudioNavigationClient.sendPoiRequest(PoiRequestPayload(requestID, PoiRequest(origin, startingRadius, 1000, Optional.empty(), Optional.of(PoiType.LANDMARK), Optional.empty())))
        val poiList = poiListQueue.take()
        minecraftClient.execute { minecraftClient.setScreen(LandmarkListScreen(parent, minecraftClient, poiList, startingRadius)) }
      })
    }
    fun openLandmarkListScreen(parent: Screen) {
      openLandmarkListScreen(parent, 64)
    }
  }
}
