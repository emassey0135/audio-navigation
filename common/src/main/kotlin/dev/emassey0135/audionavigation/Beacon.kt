package dev.emassey0135.audionavigation

import java.lang.Thread
import java.util.concurrent.ArrayBlockingQueue
import java.util.Optional
import kotlin.concurrent.thread
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
  private fun waitUntilStopped() {
    var isPlaying = true
    while (isPlaying) {
      Thread.sleep(10)
      SoundPlayer.getSourceState("beacon", { state -> isPlaying = state==AL11.AL_PLAYING })
    }
  }
  private val beaconQueue = ArrayBlockingQueue<Optional<Poi>>(16)
  private var currentBeacon: Optional<Poi> = Optional.empty()
  var isInitialized = false
  fun initialize() {
    SoundPlayer.addSource("beacon")
    SoundPlayer.setSourceMaxDistance("beacon", Configs.clientConfig.beacons.maxSoundDistance.get().toFloat())
    SoundPlayer.setSourceRolloffFactor("beacon", Configs.clientConfig.sound.rolloffFactor.get())
    isInitialized = true
    thread {
      var oldBeacon = currentBeacon
      while (true) {
        if (beaconQueue.peek()!=null) {
          oldBeacon = currentBeacon
          currentBeacon = beaconQueue.poll()
        }
        if (currentBeacon.isPresent()) {
          val minecraftClient = MinecraftClient.getInstance()
          val player = minecraftClient.player
          if (player==null)
            continue
          val origin = BlockPos.ofFloored(player.getPos())
          val orientation = Orientation(player.getRotationClient())
          SoundPlayer.updateListenerPosition()
          SoundPlayer.setSourcePosition("beacon", currentBeacon.get().pos)
          val config = Configs.clientConfig.beacons
          if (currentBeacon!=oldBeacon && config.playStartAndArrivalSounds.get()) {
            oldBeacon = currentBeacon
            Opus.playOpusFromResource("beacon", "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/Route/Route_Start.ogg")
            waitUntilStopped()
          }
          val angleBetween = Orientation.horizontalAngleBetween(origin, currentBeacon.get().pos)
          val angleDifference = orientation.horizontalDifference(angleBetween)
          val distance = currentBeacon.get().distance(origin)
          if (distance <= config.arrivalDistance.get()) {
            if (config.playStartAndArrivalSounds.get()) {
              Opus.playOpusFromResource("beacon", "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/Route/Route_End.ogg")
              waitUntilStopped()
            }
            currentBeacon = Optional.empty()
            continue
          }
          Opus.playOpusFromResource("beacon", when {
            angleDifference <= config.maxOnAxisAngle.get().toFloat() ->
            "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/${config.sound.get().onAxisSound}"
            angleDifference <= config.maxCloseToAxisAngle.get().toFloat() ->
            "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/${config.sound.get().closeToAxisSound ?: config.sound.get().offAxisSound}"
            angleDifference >= config.minBehindAngle.get().toFloat() ->
            "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/${config.sound.get().behindSound ?: config.sound.get().offAxisSound}"
            else ->
            "assets/${AudioNavigation.MOD_ID}/sounds/Beacons/${config.sound.get().offAxisSound}"
          })
          waitUntilStopped()
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
    SoundPlayer.stop("beacon")
  }
  fun isBeaconActive(): Boolean {
    return currentBeacon.isPresent()
  }
}
