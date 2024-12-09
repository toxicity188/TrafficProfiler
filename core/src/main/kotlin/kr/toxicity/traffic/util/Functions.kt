package kr.toxicity.traffic.util

import java.text.DecimalFormat

private val FORMAT = DecimalFormat("#,###")

fun Number.withFormat(): String = FORMAT.format(this)

fun <T> T?.ifNull(message: String): T & Any = this ?: throw RuntimeException(message)
fun <T> T.check(message: String, checker: T.() -> Boolean) = if (!checker()) throw RuntimeException(message) else this