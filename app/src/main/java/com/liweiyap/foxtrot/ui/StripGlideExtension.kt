package com.liweiyap.foxtrot.ui

import androidx.annotation.NonNull
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.BaseRequestOptions

@GlideExtension
object StripGlideExtension {

    @GlideOption
    @NonNull
    @JvmStatic fun defaultOptions(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
        return options
            // https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }
}