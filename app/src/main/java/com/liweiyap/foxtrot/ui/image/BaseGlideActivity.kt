package com.liweiyap.foxtrot.ui.image

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.piasy.biv.BigImageViewer
import kotlinx.coroutines.launch

abstract class BaseGlideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BigImageViewer.initialize(StripGlideImageLoader.with(applicationContext))
        mGlideCacheCleaner = GlideCacheCleaner(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()

        clearImageCache()
        BigImageViewer.imageLoader().cancelAll()
    }

    private fun clearImageCache() = lifecycleScope.launch {
        mGlideCacheCleaner.clearAllCache()
    }

    private lateinit var mGlideCacheCleaner: GlideCacheCleaner
}