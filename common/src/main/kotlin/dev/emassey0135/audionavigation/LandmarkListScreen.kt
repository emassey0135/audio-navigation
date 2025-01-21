package dev.emassey0135.audionavigation

import java.util.concurrent.SynchronousQueue
import java.util.Optional
import java.util.UUID
import kotlin.concurrent.thread
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.PoiRequestPayload
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.PoiAndDistance
import dev.emassey0135.audionavigation.PoiList
import dev.emassey0135.audionavigation.PoiType

class LandmarkListScreen(val parent: Screen, val minecraftClient: MinecraftClient, val poiList: PoiList): Screen(Text.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list")) {
  private class LandmarkEntry(val textRenderer: TextRenderer, val poi: PoiAndDistance): AlwaysSelectedEntryListWidget.Entry<LandmarkEntry>() {
    override fun getNarration(): Text {
      return Text.literal("${poi.poi.name}, ${I18n.translate("${AudioNavigation.MOD_ID}.poi_distance", poi.distance.toInt())}, ${poi.poi.positionAsNarratableString()}")
    }
    override fun render(context: DrawContext, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
      val landmarkNameText = TextWidget(x, y, entryWidth, 20, Text.literal(poi.poi.name), textRenderer)
      val landmarkDistanceText = TextWidget(x, y+30, entryWidth, 20, Text.translatable("${AudioNavigation.MOD_ID}.poi_distance", poi.distance.toInt()), textRenderer)
      val landmarkPositionText = TextWidget(x, y+60, entryWidth, 20, Text.literal(poi.poi.positionAsString()), textRenderer)
      landmarkNameText.renderWidget(context, mouseX, mouseY, tickDelta)
      landmarkDistanceText.renderWidget(context, mouseX, mouseY, tickDelta)
      landmarkPositionText.renderWidget(context, mouseX, mouseY, tickDelta)
    }
  }
  private class LandmarkList(minecraftClient: MinecraftClient, x: Int, y: Int, width: Int, height: Int, val textRenderer: TextRenderer, val poiList: PoiList): AlwaysSelectedEntryListWidget<LandmarkEntry>(minecraftClient, width, height, y, 80) {
    init {
      setX(x)
      poiList.toList().forEach { poi -> addEntry(LandmarkEntry(textRenderer, poi)) }
    }
  }
  fun goUp() {
    MinecraftClient.getInstance()?.setScreen(parent)
  }
  override fun init() {
    val landmarkList = LandmarkList(minecraftClient, 10, 10, width/2-20, height-20, textRenderer, poiList)
    addDrawableChild(landmarkList)
    addDrawableChild(ButtonWidget.builder(Text.translatable("${AudioNavigation.MOD_ID}.screens.landmark_list.back_button"), { button -> goUp() })
      .dimensions(width/2+10, 10, 50, 20)
      .build())
  }
  companion object {
    fun openLandmarkListScreen(parent: Screen) {
      thread(block = fun(): Unit {
        val minecraftClient = MinecraftClient.getInstance()
        val player = minecraftClient.player
        if (player==null)
          return
        val origin = BlockPos.ofFloored(player.getPos())
        val poiListQueue = SynchronousQueue<PoiList>()
        val requestID = UUID.randomUUID()
        AudioNavigationClient.registerPoiListHandler(requestID, { payload -> poiListQueue.put(payload.poiList) })
        AudioNavigationClient.sendPoiRequest(PoiRequestPayload(requestID, origin, 100.0, 1000, false, Optional.empty(), true, Optional.of(PoiType.LANDMARK)))
        val poiList = poiListQueue.take()
        minecraftClient.execute { minecraftClient.setScreen(LandmarkListScreen(parent, minecraftClient, poiList)) }
      })
    }
  }
}
