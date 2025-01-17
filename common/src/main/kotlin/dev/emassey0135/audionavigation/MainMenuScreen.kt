package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.AddLandmarkScreen
import dev.emassey0135.audionavigation.PoiAnnouncements

class MainMenuScreen(): Screen(Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu")) {
  private var centerX = 0
  private val buttonHeight = 20
  private val marginY = buttonHeight/4
  private var calculatedButtonY = 0
  fun buildButton(label: Text, tooltip: Text, pressAction: ButtonWidget.PressAction): ButtonWidget {
  calculatedButtonY += marginY
  return ButtonWidget.builder(label, pressAction)
    .dimensions(centerX, calculatedButtonY, textRenderer.getWidth(label)+35, buttonHeight)
    .tooltip(Tooltip.of(tooltip))
    .build()
  }
  override fun init() {
    centerX = width/2
    calculatedButtonY = height/6-marginY
    addDrawableChild(buildButton(Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.announce_nearby_pois_button"),
      Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.announce_nearby_pois_button.tooltip"),
      { button -> close(); PoiAnnouncements.triggerManualAnnouncements() }))
    addDrawableChild(buildButton(Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.add_landmark_button"),
      Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.add_landmark_button.tooltip"),
      { button -> MinecraftClient.getInstance()?.setScreen(AddLandmarkScreen(this)) }))
    addDrawableChild(buildButton(Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.open_config_screen_button"),
      Text.translatable("${AudioNavigation.MOD_ID}.screens.main_menu.open_config_screen_button.tooltip"),
      { button -> close(); ConfigApi.openScreen(AudioNavigation.MOD_ID) }))
  }
}
