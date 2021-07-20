package com.liweiyap.foxtrot.ui.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.loader.glide.GlideProgressSupport
import com.github.piasy.biv.loader.glide.ImageDownloadTarget
import com.github.piasy.biv.loader.glide.PrefetchTarget
import com.github.piasy.biv.metadata.ImageInfoExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class StripGlideImageLoader private constructor(@ApplicationContext val context: Context, okHttpClient: OkHttpClient?): ImageLoader {

    private val mFlyingRequestTargets = ConcurrentHashMap<Int, ImageDownloadTarget>(3)

    init {
        GlideProgressSupport.init(GlideApp.get(context), okHttpClient)
        GlideApp
            .get(context)
            .setMemoryCategory(MemoryCategory.LOW)
    }

    companion object {
        @JvmOverloads
        fun with(context: Context, okHttpClient: OkHttpClient? = null): StripGlideImageLoader {
            return StripGlideImageLoader(context, okHttpClient)
        }
    }

    override fun loadImage(requestId: Int, uri: Uri, callback: ImageLoader.Callback) {
        val cacheMissed = BooleanArray(1)
        val target: ImageDownloadTarget = object : ImageDownloadTarget(uri.toString()) {
            override fun onResourceReady(resource: File, transition: Transition<in File?>?) {
                super.onResourceReady(resource, transition)
                if (cacheMissed[0]) {
                    callback.onCacheMiss(ImageInfoExtractor.getImageType(resource), resource)
                } else {
                    callback.onCacheHit(ImageInfoExtractor.getImageType(resource), resource)
                }
                callback.onSuccess(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                callback.onFail(GlideLoaderException(errorDrawable))
            }

            override fun onDownloadStart() {
                cacheMissed[0] = true
                callback.onStart()
            }

            override fun onProgress(progress: Int) {
                callback.onProgress(progress)
            }

            override fun onDownloadFinish() {
                callback.onFinish()
            }
        }

        cancel(requestId)
        rememberTarget(requestId, target)
        downloadImageInto(uri, target)
    }

    override fun prefetch(uri: Uri?) {
        downloadImageInto(uri, PrefetchTarget())
    }

    @Synchronized
    override fun cancel(requestId: Int) {
        clearTarget(mFlyingRequestTargets.remove(requestId))
    }

    @Synchronized
    override fun cancelAll() {
        val targets: List<ImageDownloadTarget> = ArrayList(mFlyingRequestTargets.values)
        for (target in targets) {
            clearTarget(target)
        }
    }

    private fun downloadImageInto(uri: Uri?, target: Target<File?>) {
        GlideApp
            .with(context)
            .downloadOnly()
            .load(uri)
            .cacheFullResolutionVersion()
            .into(target)
    }

    @Synchronized
    private fun rememberTarget(requestId: Int, target: ImageDownloadTarget) {
        mFlyingRequestTargets[requestId] = target
    }

    private fun clearTarget(target: ImageDownloadTarget?) {
        if (target == null) {
            return
        }

        GlideApp
            .with(context)
            .clear(target)
    }
}