package dev.emassey0135.audionavigation.config

class ServerConfiguration(
  var restrictFeatures: Boolean,
  var allowedFeatures: List<String>,
  var radiusLimit: Int,
  var saveLandmarksOnPlayerDeath: Boolean,
  var deathLandmarksVisibleToOtherPlayers: Boolean)
