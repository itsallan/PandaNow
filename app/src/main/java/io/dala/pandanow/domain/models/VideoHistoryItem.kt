package io.dala.pandanow.domain.models

data class VideoHistoryItem(
    val videoUrl: String,
    val title: String,
    val subtitle: String?,
    val subtitleUrl: String?,
    val lastPosition: Long,
    val duration: Long,
    val timestamp: Long
)