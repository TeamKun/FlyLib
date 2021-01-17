package com.flylib.util

import java.nio.ByteBuffer
import java.util.UUID




class UUIDByteConverter {
    companion object{
        fun getBytesFromUUID(uuid: UUID): ByteArray {
            val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
            bb.putLong(uuid.mostSignificantBits)
            bb.putLong(uuid.leastSignificantBits)
            return bb.array()
        }

        fun getUUIDFromBytes(bytes: ByteArray): UUID {
            val byteBuffer: ByteBuffer = ByteBuffer.wrap(bytes)
            val high: Long = byteBuffer.long
            val low: Long = byteBuffer.long
            return UUID(high, low)
        }
    }
}