package dev.emassey0135.audionavigation.paper

import io.netty.buffer.Unpooled
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.packets.PacketIdentifiers
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

class AudioNavigationPaper(): JavaPlugin(), PluginMessageListener {
  override fun onEnable() {
    this.getServer().getMessenger().registerIncomingPluginChannel(this, PacketIdentifiers.POI_REQUEST_ID.toString(), this)
    this.getServer().getMessenger().registerOutgoingPluginChannel(this, PacketIdentifiers.POI_LIST_ID.toString())
    this.getServer().getMessenger().registerIncomingPluginChannel(this, PacketIdentifiers.ADD_LANDMARK_ID.toString(), this)
    this.getServer().getMessenger().registerIncomingPluginChannel(this, PacketIdentifiers.DELETE_LANDMARK_ID.toString(), this)
    AudioNavigation.initialize(AudioNavigationPlatformImpl())
  }
  override fun onDisable() {
    this.getServer().getMessenger().unregisterIncomingPluginChannel(this)
    this.getServer().getMessenger().unregisterOutgoingPluginChannel(this)
  }
  override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
    when (channel) {
      PacketIdentifiers.POI_REQUEST_ID.toString() -> {
        val messageBuffer = Unpooled.wrappedBuffer(message)
        val poiRequestPayload = PoiRequestPayload.CODEC.decode(messageBuffer)
        val world = (player.getWorld() as CraftWorld).getHandle()
        val poiListPayload = AudioNavigation.respondToPoiRequest(world, poiRequestPayload)
        val responseBuffer = Unpooled.buffer()
        PoiListPayload.CODEC.encode(responseBuffer, poiListPayload)
        val response = ByteArray(responseBuffer.writerIndex())
        responseBuffer.getBytes(0, response)
        player.sendPluginMessage(this, PacketIdentifiers.POI_LIST_ID.toString(), response)
      }
      PacketIdentifiers.ADD_LANDMARK_ID.toString() -> {
        val messageBuffer = Unpooled.wrappedBuffer(message)
        val addLandmarkPayload = AddLandmarkPayload.CODEC.decode(messageBuffer)
        val world = (player.getWorld() as CraftWorld).getHandle()
        AudioNavigation.addLandmark(world, addLandmarkPayload.name, addLandmarkPayload.pos)
      }
      PacketIdentifiers.DELETE_LANDMARK_ID.toString() -> {
        val messageBuffer = Unpooled.wrappedBuffer(message)
        val deleteLandmarkPayload = DeleteLandmarkPayload.CODEC.decode(messageBuffer)
        AudioNavigation.deleteLandmark(deleteLandmarkPayload.landmarkID)
      }
    }
  }
  companion object {
    @JvmField val WORLD_UUID_KEY = NamespacedKey(AudioNavigation.MOD_ID, "world_uuid")
  }
}