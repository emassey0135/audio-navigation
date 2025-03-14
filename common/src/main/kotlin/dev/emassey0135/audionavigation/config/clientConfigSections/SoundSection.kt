package dev.emassey0135.audionavigation.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import dev.emassey0135.audionavigation.features.Beacon
import dev.emassey0135.audionavigation.sound.SoundPlayer
import dev.emassey0135.audionavigation.speech.Speech

  class SoundSection: ConfigSection() {
    var maxDistance = ValidatedByte(100).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceMaxDistance("speech", value.get().toFloat()) }}
    var rolloffFactor = ValidatedFloat(0.2f).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceRolloffFactor("speech", value.get()); if (Beacon.isInitialized) SoundPlayer.setSourceRolloffFactor("beacon", value.get()) }}
  }
