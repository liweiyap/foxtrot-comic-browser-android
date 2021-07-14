package com.liweiyap.foxtrot

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.ui.StripAdapter
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
            if (mStripPager.adapter == null) {
                mStripPager.adapter = StripAdapter(database)
            }

            (mStripPager.adapter as StripAdapter).setData(database)

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

    private lateinit var mLoadingProgressIndicator: LinearProgressIndicator
    private lateinit var mStripPager: ViewPager2
}