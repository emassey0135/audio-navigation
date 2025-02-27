package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Beacon
import dev.emassey0135.audionavigation.SoundPlayer
import dev.emassey0135.audionavigation.Speech
import dev.emassey0135.audionavigation.speech.Voice

class ClientConfig: Config(Identifier.of(AudioNavigation.MOD_ID, "client_config")) {
  var announcements = AnnouncementsSection()
  class AnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedInt(25, 100, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var enableVerticalLimit = ValidatedBoolean(true)
    var verticalLimit = ValidatedInt(5, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxAnnouncements = ValidatedInt(10, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var detailedAnnouncements = ValidatedBoolean(true)
  }
  var manualAnnouncements = ManualAnnouncementsSection()
  class ManualAnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedInt(100, 100, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var enableVerticalLimit = ValidatedBoolean(true)
    var verticalLimit = ValidatedInt(10, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxAnnouncements = ValidatedInt(25, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var detailedAnnouncements = ValidatedBoolean(true)
  }
  var speech = SpeechSection()
  class SpeechSection: ConfigSection() {
    private fun updateVoiceList(synthesizers: List<String>, languages: List<String>) {
      val newVoiceList = Speech.filterVoices(synthesizers, languages)
      val oldVoiceList = voiceList.toList()
      voiceList.removeAll(oldVoiceList)
      voiceList.addAll(newVoiceList)
    }
    var synthesizers: ValidatedChoiceList<String> = ValidatedChoiceList(Speech.synthesizers(), Speech.synthesizers(), ValidatedString())
      .also { it.listenToEntry { updateVoiceList(it.get(), languages.get()) }}
    var languages: ValidatedChoiceList<String> = ValidatedChoiceList(listOf(MinecraftClient.getInstance().getLanguageManager().getLanguage().replace('_', '-')), Speech.languages(), ValidatedString())
      .also { it.listenToEntry { updateVoiceList(synthesizers.get(), it.get()) }}
    private val voiceList = Speech.filterVoices(synthesizers.get(), languages.get()).toMutableList()
    var voice = ValidatedChoice<Voice>(voiceList,
      ValidatedString().map(
        { name: String ->
          val filtered = voiceList.filter { it.name==name }
          if (filtered.isEmpty())
            voiceList.first()
          else
            filtered.first()
        },
        { voice: Voice -> voice.name }))
    var rate = ValidatedByte(50, 100, 0)
    var volume = ValidatedByte(100, 100, 0)
    var pitch = ValidatedByte(50, 100, 0)
  }
  var beacons = BeaconsSection()
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
    var arrivalDistance = ValidatedInt(1)
    var playStartAndArrivalSounds = ValidatedBoolean(true)
    var maxOnAxisAngle = ValidatedInt(15, 90, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxCloseToAxisAngle = ValidatedInt(90, 135, 15, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var minBehindAngle = ValidatedInt(150, 180, 90, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxSoundDistance = ValidatedInt(5).also { it.listenToEntry { value -> if (Beacon.isInitialized) SoundPlayer.setSourceMaxDistance("beacon", value.get().toFloat()) }}
  }
  var sound = SoundSection()
  class SoundSection: ConfigSection() {
    var maxDistance = ValidatedInt(100).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceMaxDistance("speech", value.get().toFloat()) }}
    var rolloffFactor = ValidatedFloat(0.2f).also { it.listenToEntry { value -> if (Speech.isInitialized) SoundPlayer.setSourceRolloffFactor("speech", value.get()); if (Beacon.isInitialized) SoundPlayer.setSourceRolloffFactor("beacon", value.get()) }}
  }
  companion object {
    var instance: ClientConfig? = null
    fun initialize() {
      instance = ConfigApi.registerAndLoadConfig(::ClientConfig, RegisterType.CLIENT)
    }
  }
}
