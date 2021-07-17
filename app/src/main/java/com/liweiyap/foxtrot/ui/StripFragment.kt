package com.liweiyap.foxtrot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
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
        return inflater.inflate(R.layout.viewgroup_strip, container, false)
    }

    // find Views only in onViewCreated()
    // (https://stackoverflow.com/a/38718205/12367873)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStripTitle = view.findViewById(R.id.stripTitle)
        mStripDate = view.findViewById(R.id.stripDate)
        mStripImage = view.findViewById(R.id.stripImage)
        mImageLoadProgressListener = view.findViewById(R.id.imageLoadProgressIndicator)
        mReloadMaterialButton = view.findViewById(R.id.reloadMaterialButton)

        setTitle(mStrip.title)
        setDate(mStrip.date)
        setImage(mStrip.imageSrc)
        setContentDescription(mStrip.imageAltText)

        mReloadMaterialButton.setOnClickListener {
            mReloadMaterialButton.visibility = View.INVISIBLE
            mImageLoadProgressListener.visibility = View.VISIBLE
            setImage(mStrip.imageSrc)
        }
    }

    private fun setStrip(strip: StripDataModel) {
        mStrip = strip
    }

    private fun setTitle(title: String) {
        mStripTitle.text = title
    }

    private fun setDate(date: StripDate) {
        mStripDate.text = DateFormatter.formatDate(date)
    }

    private fun setImage(imageSrc: String) {
        GlideApp
            .with(this)
            .load(imageSrc)
            .defaultOptions()
            .listener(StripGlideRequestListener(mImageOnLoadFailedCallback, mImageOnResourceReadyCallback))
            .into(mStripImage)
    }

    private fun setContentDescription(imageAltText: String) {
        mStripImage.contentDescription = imageAltText
    }

    private val mImageOnLoadFailedCallback = object : StripGlideRequestListenerCallback {
        override fun run() {
            mImageLoadProgressListener.visibility = View.INVISIBLE
            mReloadMaterialButton.visibility = View.VISIBLE
            mStripImage.setImageDrawable(null)  // https://github.com/bumptech/glide/issues/618
        }
    }

    private val mImageOnResourceReadyCallback = object : StripGlideRequestListenerCallback {
        override fun run() {
            mImageLoadProgressListener.visibility = View.INVISIBLE
        }
    }

    private lateinit var mStrip: StripDataModel
    private lateinit var mStripTitle: TextView
    private lateinit var mStripDate: TextView
    private lateinit var mStripImage: ImageView
    private lateinit var mImageLoadProgressListener: CircularProgressIndicator
    private lateinit var mReloadMaterialButton: MaterialButton
}