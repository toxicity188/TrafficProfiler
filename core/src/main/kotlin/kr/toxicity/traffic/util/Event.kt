package kr.toxicity.traffic.util

import org.bukkit.Bukkit
import org.bukkit.event.Listener

fun registerEvent(listener: Listener) {
    Bukkit.getPluginManager().registerEvents(listener, PLUGIN)
}