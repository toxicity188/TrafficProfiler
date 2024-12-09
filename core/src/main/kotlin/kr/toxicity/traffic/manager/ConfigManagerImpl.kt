package kr.toxicity.traffic.manager

import kr.toxicity.traffic.api.manager.ConfigManager
import kr.toxicity.traffic.api.manager.ConfigManager.ByteUnit
import kr.toxicity.traffic.config.PluginConfiguration
import kr.toxicity.traffic.util.warn

object ConfigManagerImpl : ConfigManager, TrafficManagerImpl {

    override val name = "config"

    private var debug = false
    private var summaryTime = 300L * 1000
    private var byteUnit = ByteUnit.KB

    override fun reload() {
        val yaml = PluginConfiguration.CONFIG.create()
        debug = yaml.getBoolean("debug", false)
        summaryTime = yaml.getLong("summary-time", 1) * 1000
        byteUnit = yaml.getString("byte-unit")?.runCatching {
            ByteUnit.valueOf(uppercase())
        }?.getOrElse {
            warn("No byte-unit value found.")
            ByteUnit.KB
        } ?: ByteUnit.KB
    }

    override fun debug(): Boolean = debug
    override fun summaryTime(): Long = summaryTime
    override fun byteUnit(): ByteUnit = byteUnit
}