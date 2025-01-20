package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import net.minecraft.util.Identifier
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.Speech

class ClientConfig: Config(Identifier.of(AudioNavigation.MOD_ID, "client_config")) {
  var announcements = AnnouncementsSection()
  class AnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedInt(25, 100, 1)
    var enableVerticalLimit = ValidatedBoolean(true)
    var verticalLimit = ValidatedInt(5, 25, 1)
    var maxAnnouncements = ValidatedInt(10, 25, 1)
    var detailedAnnouncements = ValidatedBoolean(true)
  }
  var manualAnnouncements = ManualAnnouncementsSection()
  class ManualAnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedInt(100, 100, 1)
    var enableVerticalLimit = ValidatedBoolean(true)
    var verticalLimit = ValidatedInt(10, 25, 1)
    var maxAnnouncements = ValidatedInt(25, 25, 1)
    var detailedAnnouncements = ValidatedBoolean(true)
  }
  var speech = SpeechSection()
  class SpeechSection: ConfigSection() {
    var rate = ValidatedInt(175, 900, 80).also { it.listenToEntry { value -> if (Speech.isInitialized) Speech.setRate(value.get()) }}
    var volume = ValidatedInt(200, 200, 0).also { it.listenToEntry { value -> if (Speech.isInitialized) Speech.setVolume(value.get()) }}
    var pitch = ValidatedInt(50, 100, 0).also { it.listenToEntry { value -> if (Speech.isInitialized) Speech.setPitch(value.get()) }}
    var pitchRange = ValidatedInt(50, 100, 0).also { it.listenToEntry { value -> if (Speech.isInitialized) Speech.setPitchRange(value.get()) }}
  }
  var beacons = BeaconsSection()
  class BeaconsSection: ConfigSection() {
    var maxOnAxisAngle = ValidatedInt(15, 90, 0)
  }
  var sound = SoundSection()
  class SoundSection: ConfigSection() {
    var maxDistance = ValidatedInt(100).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceMaxDistance("speech", value.get().toFloat()) }}
    var rolloffFactor = ValidatedFloat(0.2f).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceRolloffFactor("speech", value.get()) }}
  }
}
