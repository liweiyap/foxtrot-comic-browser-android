package com.liweiyap.foxtrot.ui.image

import androidx.annotation.NonNull
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.BaseRequestOptions

/**
 * For more info, see:
 * - https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
 * - https://stackoverflow.com/a/46349836/12367873
 * - https://bumptech.github.io/glide/javadocs/420/com/bumptech/glide/load/engine/DiskCacheStrategy.html
 */
@GlideExtension
object StripGlideExtension {

    @GlideOption
    @NonNull
    @JvmStatic fun cacheAll(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
        return options
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    @GlideOption
    @NonNull
    @JvmStatic fun cacheNone(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
        return options
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
    }

    @GlideOption
    @NonNull
    @JvmStatic fun cacheFullResolutionVersion(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
        return options
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }
}