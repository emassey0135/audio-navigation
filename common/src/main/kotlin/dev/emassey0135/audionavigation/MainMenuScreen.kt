package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient

class MainMenuScreen(): Screen(Text.translatable("${AudioNavigation.MOD_ID}.main_menu")) {
  override fun init() {
    addDrawableChild(ButtonWidget.builder(Text.translatable("${AudioNavigation.MOD_ID}.main_menu.speak_nearby_pois_button"), { button -> close(); AudioNavigationClient.announceNearbyPois() })
      .dimensions(width/2, 20, 200, 20)
      .tooltip(Tooltip.of(Text.translatable("${AudioNavigation.MOD_ID}.main_menu.speak_nearby_pois_button.tooltip")))
      .build())
    addDrawableChild(ButtonWidget.builder(Text.translatable("${AudioNavigation.MOD_ID}.main_menu.open_config_screen_button"), { button -> close(); ConfigApi.openScreen(AudioNavigation.MOD_ID) })
      .dimensions(width/2, 20, 100, 20)
      .tooltip(Tooltip.of(Text.translatable("${AudioNavigation.MOD_ID}.main_menu.open_config_screen_button.tooltip")))
      .build())
  }
}
