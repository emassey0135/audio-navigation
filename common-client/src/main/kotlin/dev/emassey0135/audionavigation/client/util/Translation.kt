package dev.emassey0135.audionavigation.client.util

import net.minecraft.client.resources.language.I18n
import net.minecraft.core.BlockPos
import dev.emassey0135.audionavigation.AudioNavigation
import dev.emassey0135.audionavigation.poi.Features

object Translation {
  fun positionAsString(pos: BlockPos): String {
    return "(${pos.getX().toString()}, ${pos.getY().toString()}, ${pos.getZ().toString()})"
  }
  fun positionAsNarratableString(pos: BlockPos): String {
    val x = pos.getX()
    val xString = if (x<0) I18n.get("${AudioNavigation.MOD_ID}.number.negative", -x) else x.toString()
    val y = pos.getY()
    val yString = if (y<0) I18n.get("${AudioNavigation.MOD_ID}.number.negative", -y) else y.toString()
    val z = pos.getZ()
    val zString = if (z<0) I18n.get("${AudioNavigation.MOD_ID}.number.negative", -z) else z.toString()
    return "($xString, $yString, $zString)"
  }
  fun translateFeatureName(identifier: String): String {
    val identifier = if (Features.duplicateFeatures.containsKey(identifier)) Features.duplicateFeatures.get(identifier)!! else identifier
    if (identifier in Features.features)
      return I18n.get("${AudioNavigation.MOD_ID}.features.$identifier")
    else
      return identifier
  }
}
