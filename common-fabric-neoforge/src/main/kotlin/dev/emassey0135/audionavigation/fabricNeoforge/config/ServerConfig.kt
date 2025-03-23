package dev.emassey0135.audionavigation.fabricNeoforge.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigAction
import me.fzzyhmstrs.fzzy_config.screen.widget.TextureIds
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.poi.Features

class ServerConfig: Config(ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "server_config")) {
  var allowedFeatures: ValidatedChoiceList<String> = ValidatedChoiceList(Features.features.toList(), Features.features.toList(), ValidatedString(), { identifier, _ -> Component.translatable("${AudioNavigation.MOD_ID}.features.$identifier") })
    .also { it.listenToEntry { value -> AudioNavigation.config?.allowedFeatures = value.get() }}
  var resetAllowedFeatures = ConfigAction(
    { Component.translatable("${AudioNavigation.MOD_ID}.server_config.resetAllowedFeatures") },
    { true },
    { allowedFeatures.validateAndSet(Features.features.toList()) },
    TextureIds.RESTORE,
    Component.translatable("${AudioNavigation.MOD_ID}.server_config.resetAllowedFeatures.desc"))
  var radiusLimit = ValidatedInt(67108864, 67108864, 0, ValidatedNumber.WidgetType.TEXTBOX)
    .also { it.listenToEntry { value -> AudioNavigation.config?.radiusLimit = value.get() }}
  companion object {
    var instance: ServerConfig? = null
    fun initialize() {
      instance = ConfigApi.registerAndLoadConfig(::ServerConfig)
    }
  }
}
