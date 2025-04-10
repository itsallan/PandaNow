package io.dala.pandanow.utils

fun getSpeedDescription(speed: Float): String {
    return when (speed) {
        0.25f -> "Very Slow"
        0.5f -> "Slow"
        0.75f -> "Slightly Slow"
        1f -> "Normal"
        1.25f -> "Slightly Fast"
        1.5f -> "Fast"
        1.75f -> "Very Fast"
        2f -> "Double Speed"
        else -> "${speed}x"
    }
}