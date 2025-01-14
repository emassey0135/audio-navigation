package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.util.Identifier
import dev.emassey0135.audionavigation.AudioNavigation

class ClientConfig: Config(Identifier.of(AudioNavigation.MOD_ID, "client_config")) {
  var announcementRadius = ValidatedInt(25,100,1)
  var maxAnnouncements = ValidatedInt(10,100,1)
}
