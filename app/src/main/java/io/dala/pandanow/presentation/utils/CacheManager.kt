package io.dala.pandanow.presentation.utils

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object CacheManager {
    private var simpleCache: SimpleCache? = null

    fun getCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "media")
            val cacheSize = 100 * 1024 * 1024
            val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize.toLong())
            simpleCache = SimpleCache(cacheDir, cacheEvictor)
        }
        return simpleCache!!
    }

    fun releaseCache() {
        simpleCache?.release()
        simpleCache = null
    }
}