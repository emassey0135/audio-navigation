package dev.emassey0135.audionavigation.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.config.Config
import net.minecraft.resources.ResourceLocation
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.config.clientConfigSections.AnnouncementsSection
import dev.emassey0135.audionavigation.config.clientConfigSections.BeaconsSection
import dev.emassey0135.audionavigation.config.clientConfigSections.ManualAnnouncementsSection
import dev.emassey0135.audionavigation.config.clientConfigSections.SoundSection
import dev.emassey0135.audionavigation.config.clientConfigSections.SpeechSection

class ClientConfig: Config(ResourceLocation.fromNamespaceAndPath(AudioNavigation.MOD_ID, "client_config")) {
  var announcements = AnnouncementsSection()
  var manualAnnouncements = ManualAnnouncementsSection()
  var speech = SpeechSection()
  var beacons = BeaconsSection()
  var sound = SoundSection()
  companion object {
    var instance: ClientConfig? = null
    fun initialize() {
      instance = ConfigApi.registerAndLoadConfig(::ClientConfig, RegisterType.CLIENT)
    }
  }
}
