package io.dala.pandanow.data.source

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File


@UnstableApi
class CacheLocalDataSource(
    private val context: Context
) : VideoCacheDataSource {

    companion object {
        private const val CACHE_DIR = "exo_cache"
        @Volatile private var simpleCache: SimpleCache? = null
    }

    override fun getCache(): SimpleCache {
        return simpleCache ?: synchronized(this) {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            SimpleCache(cacheDir, NoOpCacheEvictor()).also {
                simpleCache = it
            }
        }
    }
}