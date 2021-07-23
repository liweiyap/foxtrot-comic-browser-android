package com.liweiyap.foxtrot.ui.image

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * clear out Glide’s in memory cache and `BitmapPool`
 * (https://bumptech.github.io/glide/doc/caching.html)
 */
class GlideCacheCleaner @Inject constructor(@ApplicationContext private val appContext: Context) {

    suspend fun clearAllCache() {
        clearMemoryCache()
        clearDiskCache()
    }

    private suspend fun clearMemoryCache() {
        withContext(Dispatchers.Main) {
            GlideApp
                .get(appContext)
                .clearMemory()
        }
    }

    private suspend fun clearDiskCache() {
        withContext(Dispatchers.IO) {
            GlideApp
                .get(appContext)
                .clearDiskCache()
        }
    }
}
