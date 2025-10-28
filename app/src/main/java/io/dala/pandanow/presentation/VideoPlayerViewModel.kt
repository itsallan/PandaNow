package io.dala.pandanow.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
// ALIASING FIX: Alias the Media3 class to avoid conflict with the custom interface
import androidx.media3.datasource.cache.CacheDataSource as Media3CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
// IMPORT Custom Interface
import io.dala.pandanow.data.source.VideoCacheDataSource
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.usecase.GetSavedPositionUseCase
import io.dala.pandanow.domain.usecase.SaveCurrentPositionUseCase
import io.dala.pandanow.domain.usecase.SaveToHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@UnstableApi
class VideoPlayerViewModel(
    application: Application,
    videoCacheDataSource: VideoCacheDataSource,
    private val getSavedPositionUseCase: GetSavedPositionUseCase,
    private val saveCurrentPositionUseCase: SaveCurrentPositionUseCase,
    private val saveToHistoryUseCase: SaveToHistoryUseCase
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val cacheDataSourceFactory = Media3CacheDataSource.Factory()
        .setCache(videoCacheDataSource.getCache())
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
        .setFlags(Media3CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)
    private lateinit var trackSelector: DefaultTrackSelector
    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()
    private val _currentUrl = MutableStateFlow<String?>(null)
    private val currentUrl: StateFlow<String?> = _currentUrl.asStateFlow()
    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()
    private val _areControlsVisible = MutableStateFlow(true)
    val areControlsVisible = _areControlsVisible.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _isInPipMode = MutableStateFlow(false)
    val isInPipMode: StateFlow<Boolean> = _isInPipMode.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()
    private val _availableVideoQualities = MutableStateFlow<List<Pair<String, TrackSelectionParameters>>>(emptyList())
    val availableVideoQualities: StateFlow<List<Pair<String, TrackSelectionParameters>>> = _availableVideoQualities.asStateFlow()
    private val _availableSubtitles = MutableStateFlow<List<Pair<String, TrackSelectionParameters>>>(emptyList())
    val availableSubtitles: StateFlow<List<Pair<String, TrackSelectionParameters>>> = _availableSubtitles.asStateFlow()
    private val _currentVideoQuality = MutableStateFlow<Pair<String, TrackSelectionParameters>?>(null)
    val currentVideoQuality: StateFlow<Pair<String, TrackSelectionParameters>?> = _currentVideoQuality.asStateFlow()
    private val _currentSubtitle = MutableStateFlow<Pair<String, TrackSelectionParameters>?>(null)
    val currentSubtitle: StateFlow<Pair<String, TrackSelectionParameters>?> = _currentSubtitle.asStateFlow()
    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        viewModelScope.launch {
            trackSelector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
                setParameters(buildUponParameters().setPreferredTextLanguage("en"))
            }

            val newPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build()


            newPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _isBuffering.value = playbackState == Player.STATE_BUFFERING
                    if (playbackState == Player.STATE_ENDED) {
                        saveCurrentPosition()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (!isPlaying) {
                        saveCurrentPosition()
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    updateAvailableTracks(tracks)
                }

                override fun onPlayerError(error: PlaybackException) {
                    val currentUrl = _currentUrl.value
                    if (currentUrl != null && currentUrl.endsWith(".mp4", ignoreCase = true) &&
                        error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED) {
                        _errorMessage.value = "Authentication failed. Please check your credentials."
                    } else {
                        _errorMessage.value = "An error occurred: ${error.message}"
                    }
                }
            })

            newPlayer.playWhenReady = true
            newPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING

            _player.value = newPlayer
        }
    }

    fun setMediaItem(uri: String, subtitleUri: String? = null, adTagUri: String? = null) {
        _currentUrl.value = uri
        val mediaSource = buildMediaSource(uri.toUri(), adTagUri, subtitleUri)

        player.value?.setMediaSource(mediaSource)
        player.value?.prepare()

        viewModelScope.launch {
            val savedPosition = getSavedPositionUseCase(uri)
            if (savedPosition > 0) {
                player.value?.seekTo(savedPosition)
            }
            player.value?.playWhenReady = true
        }

        _errorMessage.value = null

        updateAvailableTracks(player.value?.currentTracks)
    }

    private fun buildMediaSource(uri: Uri, adTagUri: String?, subtitleUri: String?): MediaSource {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .apply { adTagUri?.let { setAdTagUri(it) } }
            .apply {
                subtitleUri?.let { uri ->
                    val subtitleConfiguration = MediaItem.SubtitleConfiguration.Builder(uri.toUri())
                        .setMimeType(MimeTypes.TEXT_VTT)
                        .setLanguage("en")
                        .build()
                    setSubtitleConfigurations(listOf(subtitleConfiguration))
                }
            }
            .build()

        val fileExtension = uri.lastPathSegment?.substringAfterLast('.', "")

        return when (fileExtension) {
            "m3u8" -> HlsMediaSource.Factory(cacheDataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
            "mpd" -> DashMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(mediaItem)
            "ism", "isml" -> SsMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(mediaItem)
            "mp4" -> {
                ProgressiveMediaSource.Factory(
                    DefaultHttpDataSource.Factory()
                )
                    .createMediaSource(mediaItem)
            }
            else -> ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(mediaItem)
        }
    }

    private fun updateAvailableTracks(tracks: Tracks?) {
        val newVideoQualities = mutableListOf<Pair<String, TrackSelectionParameters>>()
        val newSubtitles = mutableListOf<Pair<String, TrackSelectionParameters>>()

        tracks?.groups?.forEach { trackGroup ->
            when (trackGroup.type) {
                C.TRACK_TYPE_VIDEO -> {
                    for (i in 0 until trackGroup.length) {
                        if (trackGroup.isTrackSupported(i)) {
                            val format = trackGroup.getTrackFormat(i)
                            val qualityString = "${format.width}x${format.height}"
                            val parameters = trackSelector.parameters.buildUpon()
                                .setMaxVideoSize(format.width, format.height)
                                .setMaxVideoBitrate(format.bitrate)
                                .build()
                            newVideoQualities.add(qualityString to parameters)
                        }
                    }
                }
                C.TRACK_TYPE_TEXT -> {
                    for (i in 0 until trackGroup.length) {
                        if (trackGroup.isTrackSupported(i)) {
                            val format = trackGroup.getTrackFormat(i)
                            val languageString = format.language ?: "Unknown"
                            val parameters = trackSelector.parameters.buildUpon()
                                .setPreferredTextLanguage(format.language)
                                .build()
                            newSubtitles.add(languageString to parameters)
                        }
                    }
                }
            }
        }

        _availableVideoQualities.value = newVideoQualities
        if (newVideoQualities.isNotEmpty() && _currentVideoQuality.value == null) {
            _currentVideoQuality.value = newVideoQualities.first()
        }

        _availableSubtitles.value = newSubtitles
        if (newSubtitles.isNotEmpty() && _currentSubtitle.value == null) {
            _currentSubtitle.value = newSubtitles.first()
        }
    }

    fun setVideoQuality(quality: Pair<String, TrackSelectionParameters>) {
        player.value?.let { exoPlayer ->
            exoPlayer.trackSelectionParameters = quality.second
            _currentVideoQuality.value = quality
        }
    }

    fun setSubtitle(subtitle: Pair<String, TrackSelectionParameters>) {
        player.value?.let { exoPlayer ->
            exoPlayer.trackSelectionParameters = subtitle.second
            _currentSubtitle.value = subtitle
        }
    }

    fun saveToHistory(title: String, subtitle: String?, subtitleUrl: String? = null) {
        currentUrl.value?.let { url ->
            player.value?.let { exoPlayer ->
                if (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.playbackState == Player.STATE_ENDED) {
                    val currentPosition = exoPlayer.currentPosition
                    val duration = exoPlayer.duration

                    val historyItem = VideoHistoryItem(
                        videoUrl = url,
                        title = title,
                        subtitle = subtitle,
                        subtitleUrl = subtitleUrl,
                        lastPosition = currentPosition,
                        duration = duration,
                        timestamp = System.currentTimeMillis()
                    )

                    viewModelScope.launch {
                        saveToHistoryUseCase(historyItem)
                    }
                }
            }
        }
    }

    fun saveCurrentPosition() {
        player.value?.let { exoPlayer ->
            currentUrl.value?.let { url ->
                if (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.playbackState == Player.STATE_ENDED) {
                    val currentPosition = exoPlayer.currentPosition
                    val duration = exoPlayer.duration

                    viewModelScope.launch {
                        saveCurrentPositionUseCase(url, currentPosition, duration)
                    }
                }
            }
        }
    }

    fun toggleControlsVisibility() {
        _areControlsVisible.value = !_areControlsVisible.value
    }

    fun setInPipMode(inPipMode: Boolean) {
        _isInPipMode.value = inPipMode
        if (inPipMode) {
            player.value?.play()
        } else {
            saveCurrentPosition()
        }
    }

    fun showControls() {
        _areControlsVisible.value = true
    }

    fun pauseVideo() {
        player.value?.pause()
        saveCurrentPosition()
    }

    private fun resumeVideo() {
        player.value?.play()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pauseVideo()
        } else {
            resumeVideo()
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        player.value?.let { exoPlayer ->
            exoPlayer.playbackParameters = PlaybackParameters(speed)
            _playbackSpeed.value = speed
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun retryPlayback() {
        clearError()
        player.value?.prepare()
        player.value?.play()
    }

    override fun onCleared() {
        super.onCleared()
        saveCurrentPosition()
        player.value?.release()
    }
}