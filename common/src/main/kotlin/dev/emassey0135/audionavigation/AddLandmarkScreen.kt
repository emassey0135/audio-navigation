package dev.emassey0135.audionavigation

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient

class AddLandmarkScreen(val parent: Screen): Screen(Text.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark")) {
  var landmarkName = ""
  fun goUp() {
    MinecraftClient.getInstance()?.setScreen(parent)
  }
  override fun init() {
    val landmarkNameTextField = TextFieldWidget(textRenderer, 10, 10, width-20, 50, Text.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.landmark_name_text_field"))
    landmarkNameTextField.setChangedListener { text -> landmarkName = text }
    addDrawableChild(landmarkNameTextField)
    addDrawableChild(ButtonWidget.builder(Text.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.save_button"), { button -> AudioNavigationClient.addLandmark(landmarkName); close() })
      .dimensions(10, 70, 50, 20)
      .build())
    addDrawableChild(ButtonWidget.builder(Text.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.cancel_button"), { button -> goUp() })
      .dimensions(width/2+10, 70, 50, 20)
      .build())
  }
}
