package dev.emassey0135.audionavigation.client.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigAction
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.screen.widget.TextureIds
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.poi.Features

class AnnouncementsSection: ConfigSection() {
  var enableAutomaticAnnouncements = ValidatedBoolean(true)
  var announcementRadius = ValidatedByte(25, 100, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var enableVerticalLimit = ValidatedBoolean(true)
  var verticalLimit = ValidatedByte(5, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var maxAnnouncements = ValidatedByte(10, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var detailedAnnouncements = ValidatedBoolean(true)
  var includedFeatures: ValidatedChoiceList<String> = ValidatedChoiceList(Features.defaultIncludedFeatures.toList(), Features.features.toList(), ValidatedString(), { identifier, _ -> Component.translatable("${AudioNavigation.MOD_ID}.features.$identifier") })
  var resetIncludedFeatures = ConfigAction(
    { Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.resetIncludedFeatures") },
    { true },
    { includedFeatures.validateAndSet(Features.defaultIncludedFeatures.toList()) },
    TextureIds.RESTORE,
    Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.resetIncludedFeatures.desc"))
}
