fun clamp(value: Int, min: Int, max: Int): Int {
    return min.coerceAtLeast(max.coerceAtMost(value))
}

fun parseInt(value: String): Int {
    return when(value.isBlank()) { true -> 0 else -> value.toInt() }
}