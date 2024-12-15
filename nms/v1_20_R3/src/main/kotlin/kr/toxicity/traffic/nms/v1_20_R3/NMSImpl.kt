package kr.toxicity.traffic.nms.v1_20_R3

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import kr.toxicity.traffic.api.nms.*
import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class NMSImpl : NMS {
    companion object {
        private const val VANILLA_ENCODER = "encoder"
        private const val VANILLA_DECODER = "decoder"
    }

    override fun profiler(player: Player): PacketProfiler = PacketProfilerImpl(player)

    private class PacketProfilerImpl(
        private val player: Player
    ) : PacketProfiler {

        private val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
        private val record = AtomicBoolean(true)
        private val encoder: PacketEncoderDelegate = PacketEncoderDelegate(pipeline[VANILLA_ENCODER] as MessageToByteEncoder<*>) {
            record.get()
        }
        private val decoder: PacketDecoderDelegate = PacketDecoderDelegate(pipeline[VANILLA_DECODER] as ByteToMessageDecoder) {
            record.get()
        }

        @Volatile
        private var timeMills = System.currentTimeMillis()

        init {
            pipeline.replace(
                VANILLA_ENCODER,
                VANILLA_ENCODER,
                encoder
            )
            pipeline.replace(
                VANILLA_DECODER,
                VANILLA_DECODER,
                decoder
            )
        }

        override fun player(): Player = player

        @Synchronized
        override fun summary(): ProtocolResult {
            val now = System.currentTimeMillis()
            val result = ProtocolResult(now - timeMills)
            encoder(result)
            decoder(result)
            timeMills = now
            return result
        }

        @Synchronized
        override fun clear() {
            encoder.clear()
            decoder.clear()
            timeMills = System.currentTimeMillis()
        }

        override fun close() {
            clear()
        }

        override fun start() {
            record.set(true)
        }

        override fun stop() {
            record.set(false)
        }
    }

    private interface PacketDelegate : (ProtocolResult) -> Unit {
        fun clear()
    }

    private class PacketEncoderDelegate(
        val delegate : MessageToByteEncoder<*>,
        val record: () -> Boolean
    ) : MessageToByteEncoder<Packet<*>>(), PacketDelegate {
        companion object {
            private val encodeMethod = MessageToByteEncoder::class.java
                .getDeclaredMethod("encode", ChannelHandlerContext::class.java, java.lang.Object::class.java, ByteBuf::class.java)
                .apply {
                    isAccessible = true
                }
        }
        private val map = TrafficResultImpl()
        override fun encode(p0: ChannelHandlerContext, p1: Packet<*>, p2: ByteBuf) {
            encodeMethod(delegate, p0, p1, p2)
            if (record()) map.add(p1::class.java.simpleName, p2.readableBytes().toLong())
        }

        override fun invoke(p1: ProtocolResult) {
            p1.put(PacketBound.S2C, map.copy())
            map.clear()
        }

        override fun clear() {
            map.clear()
        }
    }

    private class PacketDecoderDelegate(
        val delegate: ByteToMessageDecoder,
        val record: () -> Boolean
    ) : ByteToMessageDecoder(), PacketDelegate {
        companion object {
            private val decodeMethod = ByteToMessageDecoder::class.java
                .getDeclaredMethod("decode", ChannelHandlerContext::class.java, ByteBuf::class.java, java.util.List::class.java)
                .apply {
                    isAccessible = true
                }
        }
        private val map = TrafficResultImpl()
        override fun decode(p0: ChannelHandlerContext, p1: ByteBuf, p2: MutableList<Any>) {
            val byte = p1.readableBytes()
            decodeMethod(delegate, p0, p1, p2)
            if (record() && p2.isNotEmpty()) {
                val last = p2.last() as Packet<*>
                map.add(last::class.java.simpleName, byte.toLong())
            }
        }
        override fun invoke(p1: ProtocolResult) {
            p1.put(PacketBound.C2S, map.copy())
            map.clear()
        }

        override fun clear() {
            map.clear()
        }
    }

    private class TrafficResultImpl(val map: MutableMap<String, Long>) : TrafficResult, MutableMap<String, Long> by map {
        constructor(): this(ConcurrentHashMap<String, Long>())
        private val immutableMap = Collections.unmodifiableMap(map)

        override fun get(): Map<String, Long> = immutableMap

        override operator fun plus(other: TrafficResult): TrafficResult {
            val newMap = ConcurrentHashMap(map)
            other.get().forEach { (k, v) ->
                newMap.add(k, v)
            }
            return TrafficResultImpl(newMap)
        }

        fun copy() = TrafficResultImpl(HashMap(map))

        private fun MutableMap<String, Long>.add(name: String, value: Long) {
            compute(name) { _, v ->
                v?.let { it + value } ?: value
            }
        }
        fun add(name: String, value: Long) {
            map.add(name, value)
        }
    }
}