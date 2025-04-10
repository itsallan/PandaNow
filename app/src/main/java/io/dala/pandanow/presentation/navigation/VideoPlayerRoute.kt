package io.dala.pandanow.presentation.navigation
import kotlinx.serialization.Serializable

@Serializable
data class VideoPlayerRoute(
    val videoUrl: String,
    val title: String,
    val subtitle: String?,
    val subtitleUrl: String? = null,
)