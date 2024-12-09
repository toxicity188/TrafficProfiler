package kr.toxicity.traffic.util

import kr.toxicity.traffic.api.nms.PacketBound
import kr.toxicity.traffic.api.nms.ProtocolResult
import java.text.DecimalFormat

fun ProtocolResult.toJson() = jsonObjectOf(
    "time" to timeMills,
    "client_bound" to (result[PacketBound.S2C]?.get()?.toJson() ?: jsonObjectOf()),
    "server_bound" to (result[PacketBound.C2S]?.get()?.toJson() ?: jsonObjectOf())
)

fun Map<String, Long>.toJson() = buildJsonObject {
    val newMap = entries.sortedByDescending {
        it.value
    }.associate { (k, v) ->
        k to v
    }
    val total = values.sum()
    newMap.forEach { (k, v) ->
        add(k, jsonObjectOf(
            "value" to v.applyByteUnit(),
            "percentage" to "${DecimalFormat.getInstance().format(v.toDouble() / total * 100)}%"
        ))
    }
}