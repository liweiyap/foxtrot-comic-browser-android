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
import com.liweiyap.foxtrot.util.StripDate

/**
 * When we use RecyclerView.Adapter in lieu of FragmentStateAdapter and RecyclerView.ViewHolder in lieu of Fragment,
 * we might accidentally turn on the visibility of some View like a reload button on a different ViewHolder in a StripGlideRequestListenerCallback.
 * This might be because ViewPager2::offScreenPageLimit is always > 0, so some pages in the ViewPager are loaded in advance,
 * causing the ReloadMaterialButton on the wrong page to also show up.
 *
 * For thoughts about dependency injection, see:
 * http://frogermcs.github.io/inject-everything-viewholder-and-dagger-2-example/
 * https://stackoverflow.com/questions/63697582/how-to-inject-adapter-with-hilt
 */
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

        setTitle(mStrip.title)
        setDate(mStrip.date)
        setImage(mStrip.imageSrc)
        setContentDescription(mStrip.imageAltText)

        mViewBinding.stripImageViewGroup.reloadMaterialButton.setOnClickListener {
            mViewBinding.stripImageViewGroup.reloadMaterialButton.visibility = View.INVISIBLE
            mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.VISIBLE
            setImage(mStrip.imageSrc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mViewBinding = null
    }

    private fun setStrip(strip: StripDataModel) {
        mStrip = strip
    }

    private fun setTitle(title: String) {
        mViewBinding.stripTitle.text = title
    }

    private fun setDate(date: StripDate) {
        mViewBinding.stripDate.text = DateFormatter.formatDate(date)
    }

    private fun setImage(imageSrc: String) {
        GlideApp
            .with(this)
            .load(imageSrc)
            .defaultOptions()
            .listener(StripGlideRequestListener(mImageOnLoadFailedCallback, mImageOnResourceReadyCallback))
            .into(mViewBinding.stripImageViewGroup.stripImage)
    }

    private fun setContentDescription(imageAltText: String) {
        mViewBinding.stripImageViewGroup.stripImage.contentDescription = imageAltText
    }

    private val mImageOnLoadFailedCallback = object : StripGlideRequestListenerCallback {
        override fun run() {
            mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.INVISIBLE
            mViewBinding.stripImageViewGroup.reloadMaterialButton.visibility = View.VISIBLE
            mViewBinding.stripImageViewGroup.stripImage.setImageDrawable(null)  // https://github.com/bumptech/glide/issues/618
        }
    }

    private val mImageOnResourceReadyCallback = object : StripGlideRequestListenerCallback {
        override fun run() {
            mViewBinding.stripImageViewGroup.imageLoadProgressIndicator.visibility = View.INVISIBLE
        }
    }

    private var _mViewBinding: ViewgroupStripBinding? = null
    private val mViewBinding get() = _mViewBinding!!
    private lateinit var mStrip: StripDataModel
}