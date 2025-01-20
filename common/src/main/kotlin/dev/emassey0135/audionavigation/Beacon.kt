package dev.emassey0135.audionavigation

import java.lang.Thread
import java.util.concurrent.ArrayBlockingQueue
import java.util.Optional
import kotlin.concurrent.thread
import kotlin.math.abs
import org.lwjgl.openal.AL11
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.Configs
import dev.emassey0135.audionavigation.Opus
import dev.emassey0135.audionavigation.Orientation
import dev.emassey0135.audionavigation.Poi
import dev.emassey0135.audionavigation.SoundPlayer

object Beacon {
  private val beaconQueue = ArrayBlockingQueue<Optional<Poi>>(16)
  fun initialize() {
    SoundPlayer.addSource("beacon")
    SoundPlayer.setSourceMaxDistance("beacon", 1.0f)
    thread {
      var currentBeacon: Optional<Poi> = Optional.empty()
      var isPlaying = false
      while (true) {
        if (beaconQueue.peek()!=null)
          currentBeacon = beaconQueue.poll()
        if (currentBeacon.isPresent()) {
          val minecraftClient = MinecraftClient.getInstance()
          val player = minecraftClient.player
          if (player==null)
            continue
          val origin = BlockPos.ofFloored(player.getPos())
          val orientation = Orientation(player.getRotationClient())
          SoundPlayer.updateListenerPosition()
          SoundPlayer.setSourcePosition("beacon", currentBeacon.get().pos)
          val angleBetween = Orientation.horizontalAngleBetween(origin, currentBeacon.get().pos)
          if (abs(angleBetween.horizontalAngle()-orientation.horizontalAngle()) <= Configs.clientConfig.beacons.maxOnAxisAngle.get().toFloat())
            Opus.playOpusFromResource("beacon", "assets/${AudioNavigation.MOD_ID}/sounds/beacons/Classic_OnAxis.ogg")
          else
            Opus.playOpusFromResource("beacon", "assets/${AudioNavigation.MOD_ID}/sounds/beacons/Classic_OffAxis.ogg")
          isPlaying = true
          while (isPlaying) {
            Thread.sleep(10)
            SoundPlayer.getSourceState("beacon", { state -> isPlaying = state==AL11.AL_PLAYING })
          }
        }
        else {
          Thread.sleep(10)
        }
      }
    }
  }
  fun startBeacon(poi: Poi) {
    beaconQueue.offer(Optional.of(poi))
  }
  fun stopBeacon() {
    beaconQueue.offer(Optional.empty())
  }
}
