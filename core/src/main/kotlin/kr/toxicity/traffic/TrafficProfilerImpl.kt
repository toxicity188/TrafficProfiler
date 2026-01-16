package kr.toxicity.traffic

import kr.toxicity.traffic.api.ReloadState
import kr.toxicity.traffic.api.TrafficProfiler
import kr.toxicity.traffic.api.manager.CommandManager
import kr.toxicity.traffic.api.manager.ConfigManager
import kr.toxicity.traffic.api.manager.PlayerManager
import kr.toxicity.traffic.api.nms.NMS
import kr.toxicity.traffic.manager.CommandManagerImpl
import kr.toxicity.traffic.manager.ConfigManagerImpl
import kr.toxicity.traffic.manager.PlayerManagerImpl
import kr.toxicity.traffic.manager.TrafficManagerImpl
import kr.toxicity.traffic.util.*
import org.bukkit.Bukkit
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("UNUSED")
class TrafficProfilerImpl : TrafficProfiler() {

    private val onReload = AtomicBoolean()
    private lateinit var nms: NMS

    private val managers by lazy {
        listOf(
            ConfigManagerImpl,
            PlayerManagerImpl,
            CommandManagerImpl
        )
    }

    override fun onEnable() {
        nms = when (val version = MinecraftVersion.current) {
            MinecraftVersion.version1_21_11 -> kr.toxicity.traffic.nms.v1_21_R7.NMSImpl()
            MinecraftVersion.version1_21_9, MinecraftVersion.version1_21_10 -> kr.toxicity.traffic.nms.v1_21_R6.NMSImpl()
            MinecraftVersion.version1_21_6, MinecraftVersion.version1_21_7, MinecraftVersion.version1_21_8 -> kr.toxicity.traffic.nms.v1_21_R5.NMSImpl()
            MinecraftVersion.version1_21_5 -> kr.toxicity.traffic.nms.v1_21_R4.NMSImpl()
            MinecraftVersion.version1_21_4 -> kr.toxicity.traffic.nms.v1_21_R3.NMSImpl()
            MinecraftVersion.version1_21_2, MinecraftVersion.version1_21_3 -> kr.toxicity.traffic.nms.v1_21_R2.NMSImpl()
            MinecraftVersion.version1_21, MinecraftVersion.version1_21_1 -> kr.toxicity.traffic.nms.v1_21_R1.NMSImpl()
            MinecraftVersion.version1_20_5, MinecraftVersion.version1_20_6 -> kr.toxicity.traffic.nms.v1_20_R4.NMSImpl()
            else -> {
                warn(
                    "Unsupported version: $version",
                    "Plugin will automatically disabled."
                )
                Bukkit.getPluginManager().disablePlugin(this)
                return
            }
        }
        managers.forEach(TrafficManagerImpl::start)
        reload()
        info(
            "Plugin enabled."
        )
    }

    override fun onReload(): Boolean = onReload.get()
    @Synchronized
    override fun reload(): ReloadState {
        if (onReload()) return ReloadState.ON_RELOAD
        onReload.set(true)
        val result = runCatching {
            val time = System.currentTimeMillis()
            managers.forEach {
                debug("Reloading manager: ${it.name}")
                it.reload()
            }
            ReloadState.Success(System.currentTimeMillis() - time)
        }.getOrElse {
            it.handleException("Unable to reload.")
            ReloadState.Failure(it)
        }
        onReload.set(false)
        return result
    }

    override fun onDisable() {
        managers.forEach(TrafficManagerImpl::end)
        info(
            "Plugin disabled."
        )
    }

    override fun nms(): NMS = nms

    override fun configManager(): ConfigManager = ConfigManagerImpl
    override fun playerManager(): PlayerManager = PlayerManagerImpl
    override fun commandManager(): CommandManager = CommandManagerImpl
}