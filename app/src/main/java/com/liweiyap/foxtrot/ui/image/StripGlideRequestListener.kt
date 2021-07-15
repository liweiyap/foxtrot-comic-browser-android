package com.liweiyap.foxtrot.ui.image

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class StripGlideRequestListener(
    private val onLoadFailedCallback: StripGlideRequestListenerCallback,
    private val onResourceReadyCallback: StripGlideRequestListenerCallback
): RequestListener<Drawable> {

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
        onLoadFailedCallback.run()
        return false
    }

    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        onResourceReadyCallback.run()
        return false
    }
}