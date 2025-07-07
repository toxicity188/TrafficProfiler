package kr.toxicity.traffic.util

import org.bukkit.Bukkit

data class MinecraftVersion(
    val first: Int,
    val second: Int,
    val third: Int
) : Comparable<MinecraftVersion> {
    companion object {
        val current = MinecraftVersion(Bukkit.getBukkitVersion()
            .substringBefore('-'))

        val version1_21_7 = MinecraftVersion(1, 21, 7)
        val version1_21_6 = MinecraftVersion(1, 21, 6)
        val version1_21_5 = MinecraftVersion(1, 21, 5)
        val version1_21_4 = MinecraftVersion(1, 21, 4)
        val version1_21_3 = MinecraftVersion(1, 21, 3)
        val version1_21_2 = MinecraftVersion(1, 21, 2)
        val version1_21_1 = MinecraftVersion(1, 21, 1)
        val version1_21 = MinecraftVersion(1, 21, 0)
        val version1_20_6 = MinecraftVersion(1, 20, 6)
        val version1_20_5 = MinecraftVersion(1, 20, 5)
        val version1_20_4 = MinecraftVersion(1, 20, 4)
        val version1_20_3 = MinecraftVersion(1, 20, 3)

        private val comparator = Comparator.comparing { v: MinecraftVersion ->
            v.first
        }.thenComparing { v: MinecraftVersion ->
            v.second
        }.thenComparing { v: MinecraftVersion ->
            v.third
        }
    }

    constructor(string: String): this(string.split('.'))
    constructor(string: List<String>): this(
        if (string.isNotEmpty()) string[0].toInt() else 0,
        if (string.size > 1) string[1].toInt() else 0,
        if (string.size > 2) string[2].toInt() else 0
    )
    override fun compareTo(other: MinecraftVersion): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return if (third == 0) "$first.$second" else "$first.$second.$third"
    }
}