package com.liweiyap.foxtrot

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.ui.StripFragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComicBrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoadingProgressIndicator = findViewById(R.id.loadingProgressIndicator)
        mStripPager = findViewById(R.id.stripPager)

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

            mLoadingProgressIndicator.setProgressCompat(
                (++mStripsFetched) * 100 / mViewModel.stripCountResult.value!!,
                true)
        }

        mViewModel.loadingStripDataResult.observe(this, loadingStripDataObserver)
        mViewModel.fetchAllStripData()

        mViewModel.database.observe(this, { database ->
            // AppCompatActivity extends FragmentActivity
            val pagerAdapter = StripFragmentStateAdapter(this, database)
            mStripPager.adapter = pagerAdapter
        })
    }

    private val mViewModel: ComicBrowserViewModel by viewModels()
    private var mStripsFetched: Int = 0

    private lateinit var mLoadingProgressIndicator: LinearProgressIndicator
    private lateinit var mStripPager: ViewPager2
}