package dev.emassey0135.audionavigation.client.config.clientConfigSections

import java.util.function.BiFunction
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import net.minecraft.network.chat.Component
import org.mcaccess.whisprs.metadata.Voice
import dev.emassey0135.audionavigation.client.speech.Speech

class SpeechSection: ConfigSection() {
  private fun updateVoiceList(synthesizers: List<String>, languages: List<String>) {
    val newVoiceList = Speech.filterVoices(synthesizers, languages)
    val oldVoiceList = voiceList.toList()
    voiceList.removeAll(oldVoiceList)
    voiceList.addAll(newVoiceList)
  }
  var synthesizers: ValidatedChoiceList<String> = ValidatedChoiceList(Speech.synthesizers(), Speech.synthesizers(), ValidatedString())
    .also { it.listenToEntry { updateVoiceList(it.get(), languages.get()) }}
  var languages: ValidatedChoiceList<String> = ValidatedChoiceList(Speech.defaultLanguages(), Speech.languages(), ValidatedString(), widgetType = ValidatedChoiceList.WidgetType.SCROLLABLE)
    .also { it.listenToEntry { updateVoiceList(synthesizers.get(), it.get()) }}
  private val voiceList = Speech.filterVoices(synthesizers.get(), languages.get()).toMutableList()
  private fun voiceKey(voice: Voice): String = "${voice.synthesizer.name}\u001F${voice.name}"
  var voice = ValidatedChoice<Voice>(voiceList,
    ValidatedString().map(
      { key: String ->
        voiceList.firstOrNull { voiceKey(it)==key } ?: voiceList.first()
      },
      { voice: Voice -> voiceKey(voice) }),
      translationProvider = BiFunction { voice: Voice, _: String -> Component.literal(voice.displayName) },
      widgetType = ValidatedChoice.WidgetType.SCROLLABLE)
  var rate = ValidatedByte(50, 100, 0)
  var volume = ValidatedByte(100, 100, 0)
  var pitch = ValidatedByte(50, 100, 0)
}
