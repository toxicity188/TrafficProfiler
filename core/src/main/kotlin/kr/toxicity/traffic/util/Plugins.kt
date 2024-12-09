package kr.toxicity.traffic.util

import kr.toxicity.traffic.api.TrafficProfiler
import kr.toxicity.traffic.manager.ConfigManagerImpl

val PLUGIN
    get() = TrafficProfiler.inst()

val DATA_FOLDER
    get() = PLUGIN.dataFolder.apply {
        if (!exists()) mkdirs()
    }
val DATA_PATH
    get() = PLUGIN.dataPath

fun info(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach(logger::info)
    }
}

fun warn(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach(logger::warning)
    }
}

fun debug(vararg message: String) {
    if (ConfigManagerImpl.debug()) info(*message)
}

fun Long.applyByteUnit() = this / ConfigManagerImpl.byteUnit().multiplier

fun Throwable.handleException(msg: String) {
    val list = mutableListOf(
        msg,
        "Reason: ${message ?: javaClass.simpleName}"
    )
    if (ConfigManagerImpl.debug()) list += listOf(
        "Stack trace:",
        stackTraceToString()
    )
    warn(*list.toTypedArray())
}