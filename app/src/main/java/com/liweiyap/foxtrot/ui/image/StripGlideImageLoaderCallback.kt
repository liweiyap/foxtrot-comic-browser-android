package com.liweiyap.foxtrot.ui.image

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import java.io.File

class StripGlideImageLoaderCallback(private val biv: BigImageView): ImageLoader.Callback {

    override fun onFinish() {}

    override fun onSuccess(image: File) {
        val ssiv: SubsamplingScaleImageView = biv.ssiv ?: return
        ssiv.setOnImageEventListener(SubsamplingScaleImageViewResizer(ssiv))
    }

    override fun onFail(error: Exception) {}

    override fun onCacheHit(imageType: Int, image: File) {}

    override fun onCacheMiss(imageType: Int, image: File) {}

    override fun onProgress(progress: Int) {}

    override fun onStart() {}
}