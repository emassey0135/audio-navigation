package dev.emassey0135.audionavigation.client

import java.util.UUID
import com.mojang.blaze3d.platform.InputConstants
import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.client.BalmClientRegistrars
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback
import net.blay09.mods.kuma.api.InputBinding
import net.blay09.mods.kuma.api.Kuma
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.config.ClientConfig
import dev.emassey0135.audionavigation.client.features.Beacon
import dev.emassey0135.audionavigation.client.features.PoiAnnouncements
import dev.emassey0135.audionavigation.client.screens.MainMenuScreen
import dev.emassey0135.audionavigation.client.sound.SoundPlayer
import dev.emassey0135.audionavigation.client.speech.Speech
import dev.emassey0135.audionavigation.client.util.Interval
import dev.emassey0135.audionavigation.client.util.Library
import dev.emassey0135.audionavigation.packets.AddLandmarkPayload
import dev.emassey0135.audionavigation.packets.DeleteLandmarkPayload
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

object AudioNavigationClient {
  fun sendPoiRequest(poiRequestPayload: PoiRequestPayload) {
    error("This function is not implemented.")
  }
  fun sendAddLandmark(addLandmarkPayload: AddLandmarkPayload) {
    error("This function is not implemented.")
  }
  fun sendDeleteLandmark(deleteLandmarkPayload: DeleteLandmarkPayload) {
    error("This function is not implemented.")
  }
  private val poiListHandlers = HashMap<UUID, (PoiListPayload) -> Unit>()
  fun registerPoiListHandler(requestID: UUID, handler: (PoiListPayload) -> Unit) {
    poiListHandlers.put(requestID, handler)
  }
  fun handlePoiList(payload: PoiListPayload) {
    if (poiListHandlers.containsKey(payload.requestID)) {
      val handler = poiListHandlers.get(payload.requestID)
      poiListHandlers.remove(payload.requestID)
      handler!!(payload)
    }
  }
  private val interval = Interval.sec(5)
  fun initialize(registrars: BalmClientRegistrars) {
    Library.initialize()
    Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(AudioNavigation.MOD_ID, "open_main_menu"))
      .withDefault(InputBinding.key(InputConstants.KEY_F6))
      .handleWorldInput { event ->
        Minecraft.getInstance().setScreen(MainMenuScreen())
        true
      }
      .build()
    Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(AudioNavigation.MOD_ID, "announce_nearby_pois"))
      .withDefault(InputBinding.key(InputConstants.KEY_F7))
      .handleWorldInput { event ->
        PoiAnnouncements.triggerManualAnnouncements()
        true
      }
      .build()
    Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(AudioNavigation.MOD_ID, "announce_beacon"))
      .withDefault(InputBinding.key(InputConstants.KEY_F8))
      .handleWorldInput { event ->
        Beacon.announceBeacon()
        true
      }
      .build()
    Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(AudioNavigation.MOD_ID, "stop_speech"))
      .withDefault(InputBinding.key(InputConstants.KEY_F9))
      .handleWorldInput { event ->
        Speech.interrupt()
        true
      }
      .build()
    val networking = Balm.networking()
    networking.registerClientboundPacket(PoiListPayload.ID, PoiListPayload::class.java, PoiListPayload.CODEC as StreamCodec<RegistryFriendlyByteBuf, PoiListPayload>, { player: Player, payload: PoiListPayload ->
      handlePoiList(payload)
    })
    interval.beReady()
    ClientLifecycleCallback.Started.EVENT.register { client ->
      SoundPlayer.initialize()
      Speech.initialize()
      ClientConfig.initialize()
      Speech.configure()
      Beacon.initialize()
      AudioNavigation.logger.info("Audio Navigation client has been initialized.")
    }
    ClientTickCallback.ClientLevelTick.BEFORE.register { world ->
      if (interval.isReady()) {
        PoiAnnouncements.triggerAutomaticAnnouncements()
      }
    }
  }
}
