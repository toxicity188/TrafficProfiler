package kr.toxicity.traffic.manager

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kr.toxicity.traffic.api.manager.PlayerManager
import kr.toxicity.traffic.api.nms.PacketBound
import kr.toxicity.traffic.api.nms.PacketProfiler
import kr.toxicity.traffic.util.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object PlayerManagerImpl : PlayerManager, TrafficManagerImpl {

    private val playerMap = ConcurrentHashMap<UUID, PacketProfiler>()
    private var summaryTask: ScheduledTask? = null
    private val time = AtomicLong(System.currentTimeMillis())

    override val name = "player"

    override fun start() {
        registerEvent(object : Listener {
            @EventHandler
            fun PlayerJoinEvent.join() {
                Bukkit.getRegionScheduler().runDelayed(PLUGIN, player.location, {
                    playerMap.computeIfAbsent(player.uniqueId) {
                        PLUGIN.nms().profiler(player)
                    }
                }, 1)
            }
            @EventHandler
            fun PlayerQuitEvent.quit() {
                playerMap.remove(player.uniqueId)?.close()
            }
        })
    }

    private fun resetTask() {
        time.set(System.currentTimeMillis())
        summaryTask?.cancel()
        summaryTask = Bukkit.getAsyncScheduler().runAtFixedRate(PLUGIN, {
            runCatching {
                generateProfileResult0()
            }.getOrElse {
                it.handleException("Unable to generate profile result.")
            }
        }, ConfigManagerImpl.summaryTime(), ConfigManagerImpl.summaryTime(), TimeUnit.MILLISECONDS)
    }

    override fun reload() {
        playerMap.values.forEach(PacketProfiler::clear)
        resetTask()
    }

    override fun end() {
        playerMap.values.removeIf {
            it.close()
            true
        }
    }

    override fun player(uuid: UUID): PacketProfiler? = playerMap[uuid]

    override fun generateProfileResult(): File {
        resetTask()
        return generateProfileResult0()
    }
    private fun generateProfileResult0(): File {
        val newTime = System.currentTimeMillis()
        val divMs = newTime.toDouble() - time.get()
        val div = divMs / 1000
        val clientMap = HashMap<String, Long>()
        val serverMap = HashMap<String, Long>()

        val perPlayerObject = jsonObjectOf()

        playerMap.values.forEach {
            val summary = it.summary()
            summary.result[PacketBound.S2C]?.get()?.let { map ->
                clientMap.add(map)
            }
            summary.result[PacketBound.C2S]?.get()?.let { map ->
                serverMap.add(map)
            }
            perPlayerObject.add(it.player().name, summary.toJson())
        }
        val output = DATA_PATH
            .resolve("summary")
            .resolve("${LocalDateTime.now().toString()
                .replace("([:.])".toRegex(), "-")}.json")
            .toFile()
        output.parentFile.run {
            if (!exists()) mkdirs()
        }
        val unit = ConfigManagerImpl.byteUnit().unit
        jsonObjectOf(
            "total_time" to "${divMs}ms",
            "average_traffic" to jsonObjectOf(
                "in" to "${(serverMap.values.sum().toDouble() / div).toLong()
                    .applyByteUnit()
                    .withFormat()}$unit",
                "out" to "${(clientMap.values.sum().toDouble() / div).toLong()
                    .applyByteUnit()
                    .withFormat()}$unit"
            ),
            "global" to jsonObjectOf(
                "client_bound" to clientMap.toJson(),
                "server_bound" to serverMap.toJson()
            ),
            "per_player" to perPlayerObject
        ).save(output)
        debug(
            "Summary saved at ${output.path}"
        )
        time.set(newTime)
        return output
    }

    override fun stopProfiling() {
        playerMap.values.forEach {
            it.clear()
            it.stop()
        }
    }

    override fun startProfiling() {
        playerMap.values.forEach {
            it.start()
        }
    }

    private fun MutableMap<String, Long>.add(other: Map<String, Long>) {
        other.forEach { (k, v) ->
            add(k, v)
        }
    }
    private fun MutableMap<String, Long>.add(name: String, value: Long) {
        compute(name) { _, v ->
            v?.let { it + value } ?: value
        }
    }
}