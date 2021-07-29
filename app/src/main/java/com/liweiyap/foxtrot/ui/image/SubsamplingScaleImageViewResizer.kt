package com.liweiyap.foxtrot.ui.image

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * required to scale the image properly in cases where `DisplayMetrics` report incorrect xdpi and ydpi,
 * e.g. in Pixel XL emulator. See the following GitHub issues:
 * - https://github.com/davemorrissey/subsampling-scale-image-view/issues/505
 * - https://github.com/davemorrissey/subsampling-scale-image-view/issues/536
 *
 * note that the following does not help:
 * ```
 * biv.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM)
 * biv.ssiv?.setOnImageEventListener(DisplayOptimizeListener(biv.ssiv!!))
 * ```
 */
class SubsamplingScaleImageViewResizer(private val ssiv: SubsamplingScaleImageView): SubsamplingScaleImageView.OnImageEventListener {

    override fun onReady() {
        ssiv.setDoubleTapZoomDuration(200)

        var result = 0.5f
        val imageWidth = ssiv.sWidth
        val imageHeight = ssiv.sHeight
        val viewWidth = ssiv.width
        val viewHeight = ssiv.height

        var hasZeroValue = false
        if (imageWidth == 0 || imageHeight == 0 || viewWidth == 0 || viewHeight == 0) {
            result = 0.5f
            hasZeroValue = true
        }

        val viewWHRatio = viewWidth / viewHeight.toFloat()
        val imageWHRatio = imageWidth / imageHeight.toFloat()

        if (!hasZeroValue) {
            result = if (imageWHRatio <= viewWHRatio) {
                viewWidth / imageWidth.toFloat()
            } else {
                viewHeight / imageHeight.toFloat()
            }
        }

        val maxScale = (viewWidth / imageWidth.toFloat()).coerceAtLeast(viewHeight / imageHeight.toFloat())
        if (maxScale > 1) {
            // image is smaller than screen, it should be zoomed out to its origin size
            ssiv.minScale = 1f

            // and it should be zoomed in to fill the screen
            val defaultMaxScale = ssiv.maxScale
            ssiv.maxScale = defaultMaxScale.coerceAtLeast(maxScale * 1.2f)

            val fitScreenRatio = viewWHRatio / imageWHRatio

            if (fitScreenRatio < 1.2 && fitScreenRatio > 0.9) {
                result *= 2
            }
        } else {
            // image is bigger than screen, it should be zoomed out to fit the screen
            val minScale = (viewWidth / imageWidth.toFloat()).coerceAtMost(viewHeight / imageHeight.toFloat())
            ssiv.minScale = minScale
            // but no need to set max scale
        }

        // scale to fit screen, and center
        ssiv.setDoubleTapZoomScale(result)
        ssiv.resetScaleAndCenter()
    }

    override fun onImageLoaded() {}

    override fun onPreviewLoadError(e: Exception?) {}

    override fun onImageLoadError(e: Exception?) {}

    override fun onTileLoadError(e: Exception?) {}

    override fun onPreviewReleased() {}
}