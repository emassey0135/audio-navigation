package dev.emassey0135.audionavigation.client.speech

class Voice(val synthesizer: String, val displayName: String, val name: String, val language: String) {
  override fun toString(): String {
    return displayName
  }
}
