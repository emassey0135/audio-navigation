package dev.emassey0135.audionavigation.screens

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload

object AddLandmark {
  fun addLandmark(name: String) {
    val minecraftClient = Minecraft.getInstance()
    val player = minecraftClient.player
    if (player!=null)
      AudioNavigationClient.sendAddLandmark(AddLandmarkPayload(name, BlockPos.containing(player.position())))
  }
}
class AddLandmarkScreen(val parent: Screen): Screen(Component.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark")) {
  var landmarkName = ""
  fun goUp() {
    Minecraft.getInstance()?.setScreen(parent)
  }
  override fun init() {
    val landmarkNameTextField = EditBox(font, 10, 10, width-20, 50, Component.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.landmark_name_text_field"))
    landmarkNameTextField.setHint(Component.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.landmark_name_text_field"))
    landmarkNameTextField.setResponder { text -> landmarkName = text }
    addRenderableWidget(landmarkNameTextField)
    addRenderableWidget(Button.builder(Component.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.save_button"), { button -> AddLandmark.addLandmark(landmarkName); onClose() })
      .bounds(10, 110, 50, 20)
      .build())
    addRenderableWidget(Button.builder(Component.translatable("${AudioNavigation.MOD_ID}.screens.add_landmark.cancel_button"), { button -> goUp() })
      .bounds(width/2+10, 110, 50, 20)
      .build())
  }
}
