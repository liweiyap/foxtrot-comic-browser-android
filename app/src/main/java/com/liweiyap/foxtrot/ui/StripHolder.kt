package com.liweiyap.foxtrot.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.ui.image.GlideApp
import com.liweiyap.foxtrot.ui.image.StripGlideRequestListener
import com.liweiyap.foxtrot.ui.image.StripGlideRequestListenerCallback
import com.liweiyap.foxtrot.util.DateFormatter
import com.liweiyap.foxtrot.util.StripDate

class StripHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.viewgroup_strip, parent, false)
) {
    private lateinit var mStrip: StripDataModel
    private val mStripTitle: TextView = itemView.findViewById(R.id.stripTitle)
    private val mStripDate: TextView = itemView.findViewById(R.id.stripDate)
    private val mStripImage: ImageView = itemView.findViewById(R.id.stripImage)
    private val mImageLoadProgressListener: CircularProgressIndicator = itemView.findViewById(R.id.imageLoadProgressIndicator)
    private val mRefreshMaterialButton: MaterialButton = itemView.findViewById(R.id.refreshMaterialButton)

    init {
        mRefreshMaterialButton.setOnClickListener {
            mRefreshMaterialButton.visibility = View.INVISIBLE
            mImageLoadProgressListener.visibility = View.VISIBLE
            setImage(mStrip.imageSrc)
        }
    }

    fun bind(strip: StripDataModel) {
        setStrip(strip)
        setTitle(strip.title)
        setDate(strip.date)
        setImage(strip.imageSrc)
        setContentDescription(strip.imageAltText)
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
            .with(itemView)
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
            mRefreshMaterialButton.visibility = View.VISIBLE
        }
    }

    private val mImageOnResourceReadyCallback = object : StripGlideRequestListenerCallback {
        override fun run() {
            mImageLoadProgressListener.visibility = View.INVISIBLE
            mStripImage.visibility = View.VISIBLE
        }
    }
}