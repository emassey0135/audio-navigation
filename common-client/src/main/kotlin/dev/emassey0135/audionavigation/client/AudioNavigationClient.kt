package dev.emassey0135.audionavigation.client

import java.util.Optional
import java.util.UUID
import com.mojang.blaze3d.platform.InputConstants
import net.blay09.mods.balm.Balm
import net.blay09.mods.balm.client.BalmClientRegistrars
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback
import net.blay09.mods.kuma.api.InputBinding
import net.blay09.mods.kuma.api.Kuma
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.resources.language.I18n
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.client.config.ClientConfig
import dev.emassey0135.audionavigation.client.features.Beacon
import dev.emassey0135.audionavigation.client.features.PoiAnnouncements
import dev.emassey0135.audionavigation.client.screens.MainMenuScreen
import dev.emassey0135.audionavigation.client.sound.SoundPlayer
import dev.emassey0135.audionavigation.client.speech.Speech
import dev.emassey0135.audionavigation.client.util.Interval
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.poi.Poi
import dev.emassey0135.audionavigation.poi.PoiType

object AudioNavigationClient {
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
  private var lastCompassTarget: BlockPos? = null
  private fun getCompassTarget(player: Player, level: ClientLevel): Pair<BlockPos, String>? {
    for (stack in listOf(player.mainHandItem, player.offhandItem)) {
      if (stack.item != Items.COMPASS) continue
      val lodestone = stack.get(DataComponents.LODESTONE_TRACKER)
      if (lodestone != null) {
        val globalPos = lodestone.target().orElse(null) ?: continue
        if (globalPos.dimension() == level.dimension())
          return Pair(globalPos.pos(), I18n.get("${AudioNavigation.MOD_ID}.lodestone"))
      }
      else {
        val globalPos = level.getRespawnData().globalPos()
        if (globalPos.dimension() == level.dimension())
          return Pair(globalPos.pos(), I18n.get("${AudioNavigation.MOD_ID}.spawn_point"))
      }
    }
    return null
  }
  private val interval = Interval.sec(5)
  fun initialize(registrars: BalmClientRegistrars) {
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
      if (interval.isReady())
        PoiAnnouncements.triggerAutomaticAnnouncements()
      val player = Minecraft.getInstance().player ?: return@register
      val level = Minecraft.getInstance().level ?: return@register
      val compassResult = getCompassTarget(player, level)
      if (compassResult != null) {
        val (target, name) = compassResult
        if (!Beacon.isBeaconActive() && !Beacon.compassBeaconActive)
          lastCompassTarget = null
        if ((!Beacon.isBeaconActive() || Beacon.compassBeaconActive) && target != lastCompassTarget) {
          lastCompassTarget = target
          Beacon.compassBeaconActive = true
          Beacon.startBeacon(Poi(PoiType.LANDMARK, name, target, Optional.empty()))
        }
      }
      else if (Beacon.compassBeaconActive) {
        lastCompassTarget = null
        Beacon.stopBeacon()
      }
    }
  }
}
