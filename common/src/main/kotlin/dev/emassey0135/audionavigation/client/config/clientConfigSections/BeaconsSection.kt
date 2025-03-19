package dev.emassey0135.audionavigation.client.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedShort
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.features.Beacon
import dev.emassey0135.audionavigation.client.sound.SoundPlayer

  class BeaconsSection: ConfigSection() {
    var sound = ValidatedEnum(Sound.CURRENT, ValidatedEnum.WidgetType.CYCLING)
    enum class Sound: EnumTranslatable {
      ORIGINAL {
        override val onAxisSound = "Classic/Classic_OnAxis.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Classic/Classic_OffAxis.ogg"
        override val behindSound = null
      },
      CURRENT {
        override val onAxisSound = "New/Current_A+.ogg"
        override val closeToAxisSound = "New/Current_A.ogg"
        override val offAxisSound = "New/Current_B.ogg"
        override val behindSound = "New/Current_Behind.ogg"
      },
      FLARE {
        override val onAxisSound = "Flare/Flare_A+.ogg"
        override val closeToAxisSound = "Flare/Flare_A.ogg"
        override val offAxisSound = "Flare/Flare_B.ogg"
        override val behindSound = "Flare/Flare_Behind.ogg"
      },
      SHIMMER {
        override val onAxisSound = "Shimmer/Shimmer_A+.ogg"
        override val closeToAxisSound = "Shimmer/Shimmer_A.ogg"
        override val offAxisSound = "Shimmer/Shimmer_B.ogg"
        override val behindSound = "Shimmer/Shimmer_Behind.ogg"
      },
      TACTILE {
        override val onAxisSound = "Tactile/Tactile_OnAxis.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Tactile/Tactile_OffAxis.ogg"
        override val behindSound = "Tactile/Tactile_Behind.ogg"
      },
      PING {
        override val onAxisSound = "Ping/Ping_A+.ogg"
        override val closeToAxisSound = "Ping/Ping_A.ogg"
        override val offAxisSound = "Ping/Ping_B.ogg"
        override val behindSound = "New/Current_Behind.ogg"
      },
      DROP {
        override val onAxisSound = "Drop/Drop_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Drop/Drop_A.ogg"
        override val behindSound = "Drop/Drop_Behind.ogg"
      },
      SIGNAL {
        override val onAxisSound = "Signal/Signal_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Signal/Signal_A.ogg"
        override val behindSound = "New/Current_Behind.ogg"
      },
      SIGNAL_SLOW {
        override val onAxisSound = "Signal Slow/Signal_Slow_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Signal Slow/Signal_Slow_A.ogg"
        override val behindSound = "Signal Slow/Signal_Slow_Behind.ogg"
      },
      SIGNAL_VERY_SLOW {
        override val onAxisSound = "Signal Very Slow/Signal_Very_Slow_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Signal Very Slow/Signal_Very_Slow_A.ogg"
        override val behindSound = "Signal Very Slow/Signal_Very_Slow_Behind.ogg"
      },
      MALLET {
        override val onAxisSound = "Mallet/Mallet_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Mallet/Mallet_A.ogg"
        override val behindSound = "Mallet/Mallet_Behind.ogg"
      },
      MALLET_SLOW {
        override val onAxisSound = "Mallet Slow/Mallet_Slow_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Mallet Slow/Mallet_Slow_A.ogg"
        override val behindSound = "Mallet Slow/Mallet_Slow_Behind.ogg"
      },
      MALLET_VERY_SLOW {
        override val onAxisSound = "Mallet Very Slow/Mallet_Very_Slow_A+.ogg"
        override val closeToAxisSound = null
        override val offAxisSound = "Mallet Very Slow/Mallet_Very_Slow_A.ogg"
        override val behindSound = "Mallet Very Slow/Mallet_Very_Slow_Behind.ogg"
      };
      abstract val onAxisSound: String
      abstract val closeToAxisSound: String?
      abstract val offAxisSound: String
      abstract val behindSound: String?
      override fun prefix(): String = "${AudioNavigation.MOD_ID}.client_config.beacons.sound"
    }
    var arrivalDistance = ValidatedByte(1)
    var playStartAndArrivalSounds = ValidatedBoolean(true)
    var maxOnAxisAngle = ValidatedShort(15, 90, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxCloseToAxisAngle = ValidatedShort(90, 135, 15, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var minBehindAngle = ValidatedShort(150, 180, 90, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxSoundDistance = ValidatedByte(5).also { it.listenToEntry { value -> if (Beacon.isInitialized) SoundPlayer.setSourceMaxDistance("beacon", value.get().toFloat()) }}
  }
