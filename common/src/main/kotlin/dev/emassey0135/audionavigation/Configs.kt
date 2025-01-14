package dev.emassey0135.audionavigation

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import dev.emassey0135.audionavigation.ClientConfig

object Configs {
  var clientConfig = ConfigApi.registerAndLoadConfig(::ClientConfig, RegisterType.CLIENT)
}
