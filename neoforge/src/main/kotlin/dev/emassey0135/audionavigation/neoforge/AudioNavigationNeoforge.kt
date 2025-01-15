package dev.emassey0135.audionavigation.neoforge

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.AudioNavigationClient
import dev.emassey0135.audionavigation.packets.PoiListPayload
import dev.emassey0135.audionavigation.packets.PoiRequestPayload

@Mod(AudioNavigation.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object AudioNavigationNeoforge {
  @SubscribeEvent fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
    val registrar = event.registrar("1")
    registrar.playToServer(PoiRequestPayload.ID, PoiRequestPayload.CODEC, { payload: PoiRequestPayload, context: IPayloadContext ->
        context.reply(AudioNavigation.respondToPoiRequest(payload))
      })
    registrar.playToClient(PoiListPayload.ID, PoiListPayload.CODEC, { payload: PoiListPayload, context: IPayloadContext ->
        AudioNavigationClient.handlePoiList(payload)
      })
  }
  init {
    AudioNavigation.initialize()
  }
}