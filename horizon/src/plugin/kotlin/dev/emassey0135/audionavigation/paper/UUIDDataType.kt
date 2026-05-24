package dev.emassey0135.audionavigation.paper

import java.nio.ByteBuffer
import java.util.UUID
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import net.minecraft.core.UUIDUtil

class UUIDDataType: PersistentDataType<ByteArray, UUID> {
  override fun getPrimitiveType(): Class<ByteArray> {
    return ByteArray::class.java
  }
  override fun getComplexType(): Class<UUID> {
    return UUID::class.java
  }
  override fun toPrimitive(complex: UUID, context: PersistentDataAdapterContext): ByteArray {
    return UUIDUtil.uuidToByteArray(complex)
  }
  override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): UUID {
    val buffer = ByteBuffer.wrap(primitive)
    val first = buffer.getLong()
    val second = buffer.getLong()
    return UUID(first, second)
  }
}
