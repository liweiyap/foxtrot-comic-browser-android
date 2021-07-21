package com.liweiyap.foxtrot.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.piasy.biv.BigImageViewer
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.databinding.ViewgroupStripBinding
import com.liweiyap.foxtrot.ui.image.DownloadProgressPieIndicator
import com.liweiyap.foxtrot.ui.image.StripGlideImageLoader
import com.liweiyap.foxtrot.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Impracticality in using the following (?):
 *
 * 1. androidx.fragment.ViewModel, because ViewModel's only responsibility is to manage the data for the UI.
 *    It should never access your view hierarchy or hold a reference back to the Activity or the Fragment.
 *    Whilst we certainly don't store references in ViewModel, a potential setImage() function would involve
 *    GlideApp loading a Target image into an (Image)View asynchronously, so I just wanna be safe and guarantee
 *    no leaks.
 *
 * 2. Builder design pattern, because, similarly, it is awkward to pass the Fragment argument as well as the
 *    logic of the StripGlideRequestListener into the Builder. Moreover, we would also have to pass
 *    _mViewBinding into some BuilderPlan class, but the lifetime of this variable is tied to the
 *    lifetime of the Fragment, since the variable is destroyed in onDestroyView().
 *
 * Hence, it would be simpler to keep all View-related logic in this class.
 */
@AndroidEntryPoint
class StripFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BigImageViewer.initialize(StripGlideImageLoader.with(requireActivity().applicationContext))

        val args: Bundle = arguments
            ?: throw RuntimeException("StripFragment::onCreateView(): No arguments passed in")

        val strip: StripDataModel = args.getParcelable(requireActivity().resources.getString(R.string.stripDataModel_fragmentParcelable))
            ?: throw RuntimeException("StripFragment::onCreateView(): No StripDataModel argument passed in")

        setStrip(strip)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        mViewBinding.stripImage.setProgressIndicator(DownloadProgressPieIndicator())
        mViewBinding.stripImage.showImage(Uri.parse(mStrip.imageSrc))
    }

    private fun setContentDescription() {
        mViewBinding.stripImage.contentDescription = mStrip.imageAltText
    }

    private var _mViewBinding: ViewgroupStripBinding? = null
    private val mViewBinding get() = _mViewBinding!!
    private lateinit var mStrip: StripDataModel
}