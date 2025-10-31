package io.dala.pandanow.domain.models

data class Playlist(
    val id: String,
    val name: String,
    val videos: List<VideoHistoryItem>,
    val createdTimestamp: Long
)