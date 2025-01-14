package dev.emassey0135.audionavigation

import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import dev.emassey0135.audionavigation.Database

object AudioNavigation {
  const val MOD_ID = "audio_navigation"
  @JvmField val logger = LoggerFactory.getLogger("audio-navigation")
  fun initialize() {
    logger.info("Audio Navigation has been initialized.")
  }
}
