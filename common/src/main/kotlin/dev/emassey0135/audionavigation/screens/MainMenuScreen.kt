package dev.emassey0135.audionavigation.screens

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.features.Beacon
import dev.emassey0135.audionavigation.features.PoiAnnouncements

class MainMenuScreen(): Screen(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu")) {
  private var centerX = 0
  private val buttonHeight = 20
  private val marginY = buttonHeight*3
  private var calculatedButtonY = 0
  fun buildButton(label: Component, tooltip: Component, onPress: Button.OnPress): Button {
  calculatedButtonY += marginY
  return Button.builder(label, onPress)
    .bounds(centerX, calculatedButtonY, font.width(label)+35, buttonHeight)
    .tooltip(Tooltip.create(tooltip))
    .build()
  }
  override fun init() {
    centerX = width/2
    calculatedButtonY = height/6-marginY
    addRenderableWidget(buildButton(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.announce_nearby_pois_button"),
      Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.announce_nearby_pois_button.tooltip"),
      { button -> onClose(); PoiAnnouncements.triggerManualAnnouncements() }))
    if (Beacon.isBeaconActive())
      addRenderableWidget(buildButton(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.stop_beacon_button"),
        Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.stop_beacon_button.tooltip"),
        { button -> onClose(); Beacon.stopBeacon() }))
    addRenderableWidget(buildButton(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.add_landmark_button"),
      Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.add_landmark_button.tooltip"),
      { button -> Minecraft.getInstance()?.setScreen(AddLandmarkScreen(this)) }))
    addRenderableWidget(buildButton(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.landmark_list_button"),
      Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.landmark_list_button.tooltip"),
      { button -> LandmarkListScreen.openLandmarkListScreen(this) }))
    addRenderableWidget(buildButton(Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.open_config_screen_button"),
      Component.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.open_config_screen_button.tooltip"),
      { button -> onClose(); ConfigApi.openScreen(AudioNavigation.MOD_ID) }))
  }
}
