package io.dala.pandanow.utils

fun getQualityDescription(quality: String): String {
    return when {
        quality.contains("2160") -> "4K"
        quality.contains("1080") -> "Full HD"
        quality.contains("720") -> "HD"
        quality.contains("480") -> "SD"
        quality.contains("360") -> "Low"
        quality.contains("240") -> "Very Low"
        quality.equals("Auto", ignoreCase = true) -> "Auto"
        else -> quality
    }
}
