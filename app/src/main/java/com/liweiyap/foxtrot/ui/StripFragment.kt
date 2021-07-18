package com.liweiyap.foxtrot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.databinding.ViewgroupStripBinding
import com.liweiyap.foxtrot.ui.image.GlideApp
import com.liweiyap.foxtrot.ui.image.StripGlideRequestListener
import com.liweiyap.foxtrot.ui.image.StripGlideRequestListenerCallback
import com.liweiyap.foxtrot.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Impracticality in using the following (?):
 * 1. androidx.fragment.ViewModel, because ViewModel's only responsibility is to manage the data for the UI.
 *    It should never access your view hierarchy or hold a reference back to the Activity or the Fragment.
 *    Whilst we certainly don't store references in ViewModel, a potential setImage() function would involve
 *    GlideApp loading a Target image into an (Image)View asynchronously, so I just wanna be safe.
 * 2. Builder design pattern, because we would have to pass _mViewBinding into some BuilderPlan class,
 *    but the lifetime of this variable is tied to the lifetime of the Fragment, since the variable
 *    is destroyed in onDestroyView().
 */
@AndroidEntryPoint
class StripFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val args: Bundle = arguments
            ?: throw RuntimeException("StripFragment::onCreateView(): No arguments passed in")

        val strip: StripDataModel = args.getParcelable(requireActivity().resources.getString(R.string.stripDataModel_fragmentParcelable))
            ?: throw RuntimeException("StripFragment::onCreateView(): No StripDataModel argument passed in")

        setStrip(strip)
        _mViewBinding = ViewgroupStripBinding.inflate(inflater, container, false)
        return mViewBinding.root
    }

    // find Views only in onViewCreated()
    // (https://stackoverflow.com/a/38718205/12367873)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle()
        setDate()
        setImage()
        setContentDescription()

        setReloadButtonOnClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mViewBinding = null
    }

    private fun setStrip(strip: StripDataModel) {
        mStrip = strip
    }

    private fun setTitle() {
        mViewBinding.stripTitle.text = mStrip.title
    }

    private fun setDate() {
        mViewBinding.stripDate.text = DateFormatter.formatDate(mStrip.date)
    }

    private fun setImage() {
        val imageOnLoadFailedCallback = object : StripGlideRequestListenerCallback {
            override fun run() {
                mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.INVISIBLE
                mViewBinding.stripImageViewGroup.reloadButton.visibility = View.VISIBLE
                mViewBinding.stripImageViewGroup.stripImage.setImageDrawable(null)  // https://github.com/bumptech/glide/issues/618
            }
        }

        val imageOnResourceReadyCallback = object : StripGlideRequestListenerCallback {
            override fun run() {
                mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.INVISIBLE
            }
        }

        GlideApp
            .with(this)
            .load(mStrip.imageSrc)  // loading by Glide's RequestManager is done asynchronously (https://github.com/bumptech/glide/issues/1209#issuecomment-219548423)
            .noCache()
            .listener(StripGlideRequestListener(imageOnLoadFailedCallback, imageOnResourceReadyCallback))
            .into(mViewBinding.stripImageViewGroup.stripImage)
    }

    private fun setContentDescription() {
        mViewBinding.stripImageViewGroup.stripImage.contentDescription = mStrip.imageAltText
    }

    private fun setReloadButtonOnClickListener() {
        // also, check out:
        // https://stackoverflow.com/questions/55926038/how-to-handle-onclick-or-ontouch-like-events-in-viewmodel-with-data-binding-in-m
        // https://developer.android.com/topic/libraries/data-binding/expressions#listener_bindings
        mViewBinding.stripImageViewGroup.reloadButton.setOnClickListener {
            mViewBinding.stripImageViewGroup.reloadButton.visibility = View.INVISIBLE
            mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.VISIBLE
            setImage()
        }
    }

    private var _mViewBinding: ViewgroupStripBinding? = null
    private val mViewBinding get() = _mViewBinding!!
    private lateinit var mStrip: StripDataModel
}