package kr.toxicity.traffic.util

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

fun File.toYaml() = YamlConfiguration.loadConfiguration(this)
fun InputStream.toYaml() = InputStreamReader(this).buffered().use {
    YamlConfiguration.loadConfiguration(it)
}