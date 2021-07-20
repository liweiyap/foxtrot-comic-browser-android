package com.liweiyap.foxtrot.ui.image

import android.view.LayoutInflater
import android.view.View
import com.filippudak.ProgressPieView.ProgressPieView
import com.github.piasy.biv.indicator.ProgressIndicator
import com.github.piasy.biv.view.BigImageView
import com.liweiyap.foxtrot.R
import java.util.*

class DownloadProgressPieIndicator : ProgressIndicator {

    private lateinit var mProgressPieView: ProgressPieView

    override fun getView(parent: BigImageView): View {
        mProgressPieView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_download_progress_pie_indicator, parent, false) as ProgressPieView
        return mProgressPieView
    }

    override fun onStart() {}

    override fun onProgress(progress: Int) {
        if (progress < 0 || progress > 100) {
            return
        }
        mProgressPieView.progress = progress
        mProgressPieView.text = String.format(Locale.getDefault(), "%d%%", progress)
    }

    override fun onFinish() {}
}