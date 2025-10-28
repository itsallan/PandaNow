package io.dala.pandanow.data.source

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache

interface VideoCacheDataSource {
    @OptIn(UnstableApi::class)
    fun getCache(): SimpleCache
}