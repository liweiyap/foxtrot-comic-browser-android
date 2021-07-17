package com.liweiyap.foxtrot

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
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

        mStripFetchProgressIndicator = findViewById(R.id.stripFetchProgressIndicator)
        mStripPager = findViewById(R.id.stripPager)

        val stripCountObserver = Observer<Int?> { count ->
            // To avoid IllegalStateException: Cannot switch to indeterminate mode while the progress indicator is visible.
            // (see: https://github.com/material-components/material-components-android/issues/1921)
            if (count == null) {
                mStripFetchProgressIndicator.isVisible = false
                mStripFetchProgressIndicator.isIndeterminate = true
                mStripFetchProgressIndicator.isVisible = true
                return@Observer
            }

            mStripFetchProgressIndicator.isIndeterminate = false
        }

        mViewModel.stripCountResult.observe(this, stripCountObserver)
        mViewModel.countStrips()

        val fetchingStripDataObserver = Observer<StripDataModel?> { fetchedStrip ->
            if (fetchedStrip == null) {
                mStripFetchProgressIndicator.hide()
                return@Observer
            }

            if ( (mViewModel.stripCountResult.value == null) || (mViewModel.stripCountResult.value == 0) ) {
                return@Observer
            }

            mStripFetchProgressIndicator.setProgressCompat(
                (++mStripsFetched) * 100 / mViewModel.stripCountResult.value!!,
                true)
        }

        mViewModel.fetchingStripDataResult.observe(this, fetchingStripDataObserver)
        mViewModel.fetchAllStripData()

        mViewModel.database.observe(this, { database ->
            if (mStripPager.adapter == null) {
                mStripPager.adapter = StripFragmentStateAdapter(this, database)
            }

            (mStripPager.adapter as StripFragmentStateAdapter).setData(database)

            DiffUtil.calculateDiff(object : DiffUtil.Callback(){
                override fun getOldListSize() = mOldDatabase.size

                override fun getNewListSize() = database.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    mOldDatabase[oldItemPosition] == database[newItemPosition]

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    areItemsTheSame(oldItemPosition, newItemPosition)
            }, false).dispatchUpdatesTo(mStripPager.adapter!!)

            mOldDatabase = database
        })
    }

    private val mViewModel: ComicBrowserViewModel by viewModels()
    private var mStripsFetched: Int = 0
    private var mOldDatabase: List<StripDataModel> = listOf()

    private lateinit var mStripFetchProgressIndicator: LinearProgressIndicator
    private lateinit var mStripPager: ViewPager2
}