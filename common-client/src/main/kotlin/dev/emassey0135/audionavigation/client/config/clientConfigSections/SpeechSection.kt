package dev.emassey0135.audionavigation.client.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import net.minecraft.client.Minecraft
import dev.emassey0135.audionavigation.client.speech.Speech
import dev.emassey0135.audionavigation.client.speech.Voice

  class SpeechSection: ConfigSection() {
    private fun updateVoiceList(synthesizers: List<String>, languages: List<String>) {
      val newVoiceList = Speech.filterVoices(synthesizers, languages)
      val oldVoiceList = voiceList.toList()
      voiceList.removeAll(oldVoiceList)
      voiceList.addAll(newVoiceList)
    }
    var synthesizers: ValidatedChoiceList<String> = ValidatedChoiceList(Speech.synthesizers(), Speech.synthesizers(), ValidatedString())
      .also { it.listenToEntry { updateVoiceList(it.get(), languages.get()) }}
    var languages: ValidatedChoiceList<String> = ValidatedChoiceList(listOf(Minecraft.getInstance().getLanguageManager().getSelected().replace('_', '-')), Speech.languages(), ValidatedString())
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
