package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.liweiyap.foxtrot.database.StripDataModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComicBrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoadingProgressIndicator = findViewById(R.id.loadingProgressIndicator)

        val stripCountObserver = Observer<Int?> { count ->
            mLoadingProgressIndicator.isIndeterminate = (count == null)
        }

        mViewModel.stripCountResult.observe(this, stripCountObserver)
        mViewModel.countStrips()

        val loadingStripDataObserver = Observer<StripDataModel?> { fetchedStrip ->
            if (fetchedStrip == null) {
                mLoadingProgressIndicator.hide()
                return@Observer
            }

            if ( (mViewModel.stripCountResult.value == null) || (mViewModel.stripCountResult.value == 0) ) {
                return@Observer
            }

            mLoadingProgressIndicator.setProgressCompat((++mStripsFetched) * 100 / mViewModel.stripCountResult.value!!, true)
        }

        mViewModel.loadingStripDataResult.observe(this, loadingStripDataObserver)
        mViewModel.fetchAllStripData()

        val displayedStripDataObserver = Observer<StripDataModel?> { fetchedStrip ->
            if (fetchedStrip == null) {
                return@Observer
            }

            val tv: TextView = findViewById(R.id.hello_world)
            tv.text = fetchedStrip.date.toString()
        }

        mViewModel.displayedStripDataResult.observe(this, displayedStripDataObserver)
        mViewModel.fetchLatestStripData()
    }

    private val mViewModel: ComicBrowserViewModel by viewModels()
    private var mStripsFetched: Int = 0

    private lateinit var mLoadingProgressIndicator: LinearProgressIndicator
}