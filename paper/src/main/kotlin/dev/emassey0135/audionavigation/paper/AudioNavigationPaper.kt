package dev.emassey0135.audionavigation.paper

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import dev.emassey0135.audionavigation.AudioNavigation

class AudioNavigationPaper(): JavaPlugin(), Listener {
  override fun onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this)
    AudioNavigation.initialize(AudioNavigationPlatformImpl())
  }
  @EventHandler
  fun onPlayerJoin(event: PlayerJoinEvent) {
    event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"))
  }
  companion object {
    @JvmField val WORLD_UUID_KEY = NamespacedKey(AudioNavigation.MOD_ID, "world_uuid")
  }
}