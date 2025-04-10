package io.dala.pandanow.data

import kotlinx.serialization.Serializable

@Serializable
data class VideoHistoryItem(
    val videoUrl: String,
    val title: String,
    val subtitle: String?,
    val subtitleUrl: String? = null,
    val lastPosition: Long = 0,
    val duration: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)