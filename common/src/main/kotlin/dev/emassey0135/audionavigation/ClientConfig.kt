package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.util.Identifier
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Speech

class ClientConfig: Config(Identifier.of(AudioNavigation.MOD_ID, "client_config")) {
  var announcements = AnnouncementsSection()
  class AnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedInt(25, 100, 1)
    var maxAnnouncements = ValidatedInt(10, 100, 1)
    var detailedAnnouncements = ValidatedBoolean(true)
  }
  var speech = SpeechSection()
  class SpeechSection: ConfigSection() {
    var rate = ValidatedInt(175, 900, 80).also { it.addListener { value -> Speech.setRate(value.get()) }}
    var volume = ValidatedInt(200, 200, 0).also { it.addListener { value -> Speech.setVolume(value.get()) }}
    var pitch = ValidatedInt(50, 100, 0).also { it.addListener { value -> Speech.setPitch(value.get()) }}
    var pitchRange = ValidatedInt(50, 100, 0).also { it.addListener { value -> Speech.setPitchRange(value.get()) }}
  }
}
