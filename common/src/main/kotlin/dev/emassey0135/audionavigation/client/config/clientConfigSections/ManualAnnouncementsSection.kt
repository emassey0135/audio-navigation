package dev.emassey0135.audionavigation.client.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.poi.Features

  class ManualAnnouncementsSection: ConfigSection() {
    var announcementRadius = ValidatedByte(100, 100, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var enableVerticalLimit = ValidatedBoolean(true)
    var verticalLimit = ValidatedByte(10, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var maxAnnouncements = ValidatedByte(25, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
    var detailedAnnouncements = ValidatedBoolean(true)
    var includedFeatures: ValidatedChoiceList<String> = ValidatedChoiceList(Features.defaultIncludedFeatures.toList(), Features.features.toList(), ValidatedString(), { identifier, _ -> Component.translatable("${AudioNavigation.MOD_ID}.features.$identifier") })
  }
