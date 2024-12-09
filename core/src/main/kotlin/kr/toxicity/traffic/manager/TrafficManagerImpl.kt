package kr.toxicity.traffic.manager

import kr.toxicity.traffic.api.manager.TrafficManager

interface TrafficManagerImpl : TrafficManager {

    val name: String

    fun start() {}
    fun end() {}
}