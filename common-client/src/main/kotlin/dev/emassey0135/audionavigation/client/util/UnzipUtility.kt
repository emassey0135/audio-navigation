package dev.emassey0135.audionavigation.client.util

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream

/* This code has been derived from Minecraft Access (https://github.com/minecraft-access/minecraft-access).
   The project is released under the GPL-3.0; see LICENSE.minecraft-access
*/

object UnzipUtility {
  private const val BUFFER_SIZE = 4096

  fun unzip(zipFilePath: String, destDirectory: String) {
    val destDir = File(destDirectory).canonicalFile
    destDir.mkdirs()
    ZipInputStream(FileInputStream(zipFilePath)).use { zipIn ->
      var entry = zipIn.nextEntry
      while (entry != null) {
        val resolved = File(destDir, entry.name).canonicalFile
        if (!resolved.path.startsWith(destDir.path + File.separator))
          throw IOException("Entry is outside of the target directory: ${entry.name}")
        if (entry.isDirectory) {
          resolved.mkdirs()
        } else {
          resolved.parentFile?.mkdirs()
          BufferedOutputStream(FileOutputStream(resolved), BUFFER_SIZE).use { bos ->
            val buf = ByteArray(BUFFER_SIZE)
            var read = zipIn.read(buf)
            while (read != -1) {
              bos.write(buf, 0, read)
              read = zipIn.read(buf)
            }
          }
        }
        zipIn.closeEntry()
        entry = zipIn.nextEntry
      }
    }
  }
}
