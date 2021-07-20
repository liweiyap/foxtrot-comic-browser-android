package com.liweiyap.foxtrot

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.databinding.ActivityComicBrowserBinding
import com.liweiyap.foxtrot.ui.StripFragmentStateAdapter
import com.liweiyap.foxtrot.ui.image.GlideCacheCleaner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ComicBrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityComicBrowserBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        val stripCountObserver = Observer<Int?> { count ->
            // To avoid IllegalStateException: Cannot switch to indeterminate mode while the progress indicator is visible.
            // (see: https://github.com/material-components/material-components-android/issues/1921)
            if (count == null) {
                mViewBinding.stripFetchProgressIndicator.isVisible = false
                mViewBinding.stripFetchProgressIndicator.isIndeterminate = true
                mViewBinding.stripFetchProgressIndicator.isVisible = true
                return@Observer
            }

            mViewBinding.stripFetchProgressIndicator.isIndeterminate = false
        }

        mViewModel.stripCountResult.observe(this, stripCountObserver)
        mViewModel.countStrips()

        val fetchingStripDataObserver = Observer<StripDataModel?> { fetchedStrip ->
            if (fetchedStrip == null) {
                mViewBinding.stripFetchProgressIndicator.hide()
                return@Observer
            }

            if ( (mViewModel.stripCountResult.value == null) || (mViewModel.stripCountResult.value == 0) ) {
                return@Observer
            }

            mViewBinding.stripFetchProgressIndicator.setProgressCompat(
                (++mStripsFetched) * 100 / mViewModel.stripCountResult.value!!,
                true)
        }

        mViewModel.fetchingStripDataResult.observe(this, fetchingStripDataObserver)
        mViewModel.fetchAllStripData()

        mViewModel.database.observe(this, { database ->
            if (mViewBinding.stripPager.adapter == null) {
                mViewBinding.stripPager.adapter = StripFragmentStateAdapter(this, database)
            }

            (mViewBinding.stripPager.adapter as StripFragmentStateAdapter).setData(database)

            DiffUtil.calculateDiff(object : DiffUtil.Callback(){
                override fun getOldListSize() = mOldDatabase.size

                override fun getNewListSize() = database.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    mOldDatabase[oldItemPosition] == database[newItemPosition]

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    areItemsTheSame(oldItemPosition, newItemPosition)
            }, false).dispatchUpdatesTo(mViewBinding.stripPager.adapter!!)

            mOldDatabase = database
        })

        mGlideCacheCleaner = GlideCacheCleaner(applicationContext)
    }

    override fun onDestroy() {
        clearImageCache()
        super.onDestroy()
    }

    private fun clearImageCache() = lifecycleScope.launch {
        mGlideCacheCleaner.clearAllCache()
    }

    private lateinit var mViewBinding: ActivityComicBrowserBinding
    private val mViewModel: ComicBrowserViewModel by viewModels()
    private var mStripsFetched: Int = 0
    private var mOldDatabase: List<StripDataModel> = listOf()

    @Inject lateinit var mGlideCacheCleaner: GlideCacheCleaner
}