package dev.emassey0135.audionavigation.client.config.clientConfigSections

import me.fzzyhmstrs.fzzy_config.config.ConfigAction
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.screen.widget.TextureIds
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedByte
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.network.chat.Component
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.util.Orientation
import dev.emassey0135.audionavigation.poi.Features

class AnnouncementsSection: ConfigSection() {
  var enableAutomaticAnnouncements = ValidatedBoolean(true)
  var announcementRadius = ValidatedByte(25, 100, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var enableVerticalLimit = ValidatedBoolean(true)
  var verticalLimit = ValidatedByte(5, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var maxAnnouncements = ValidatedByte(10, 25, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
  var announceDistance = ValidatedBoolean(true)
  var announceDirection = ValidatedBoolean(false)
  var includeVerticalDirection = ValidatedBoolean(true)
  var horizontalDirectionType = ValidatedEnum(Orientation.HorizontalDirectionType.CLOCK_HAND, ValidatedEnum.WidgetType.CYCLING)
  var verticalDirectionType = ValidatedEnum(Orientation.VerticalDirectionType.DIRECTION_AND_ANGLE, ValidatedEnum.WidgetType.CYCLING)
  var includedFeatures: ValidatedChoiceList<String> = ValidatedChoiceList(Features.defaultIncludedFeatures.toList(), Features.features.toList(), ValidatedString(), { identifier, _ -> Component.translatable("${AudioNavigation.MOD_ID}.features.$identifier") }, widgetType = ValidatedChoiceList.WidgetType.SCROLLABLE)
  var resetIncludedFeatures = ConfigAction(
    { Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.resetIncludedFeatures") },
    { true },
    { includedFeatures.validateAndSet(Features.defaultIncludedFeatures.toList()) },
    TextureIds.RESTORE,
    Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.resetIncludedFeatures.desc"))
  var disableAllIncludedFeatures = ConfigAction(
    { Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.disableAllIncludedFeatures") },
    { true },
    { includedFeatures.validateAndSet(listOf()) },
    TextureIds.RESTORE,
    Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.disableAllIncludedFeatures.desc"))
  var enableAllIncludedFeatures = ConfigAction(
    { Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.enableAllIncludedFeatures") },
    { true },
    { includedFeatures.validateAndSet(Features.features.toList()) },
    TextureIds.RESTORE,
    Component.translatable("${AudioNavigation.MOD_ID}.client_config.announcements.enableAllIncludedFeatures.desc"))
}
