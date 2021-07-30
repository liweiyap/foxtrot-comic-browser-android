package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.databinding.ActivityComicBrowserBinding
import com.liweiyap.foxtrot.ui.StripFragmentStateAdapter
import com.liweiyap.foxtrot.ui.image.BaseGlideActivity
import com.liweiyap.foxtrot.util.OnFavouriteChangeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComicBrowserActivity : BaseGlideActivity(), OnFavouriteChangeListener {

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

        mViewModel.stripDatabase.observe(this, { database ->
            if (mViewBinding.stripPager.adapter == null) {
                mViewBinding.stripPager.adapter = StripFragmentStateAdapter(this, database)
            }

            (mViewBinding.stripPager.adapter as StripFragmentStateAdapter).setData(database)

            DiffUtil.calculateDiff(object : DiffUtil.Callback(){
                override fun getOldListSize() = mOldDatabase.size

                override fun getNewListSize() = database.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return ((mOldDatabase[oldItemPosition].url == database[newItemPosition].url) &&
                            (mOldDatabase[oldItemPosition].title == database[newItemPosition].title) &&
                            (mOldDatabase[oldItemPosition].date == database[newItemPosition].date) &&
                            (mOldDatabase[oldItemPosition].imageSrc == database[newItemPosition].imageSrc) &&
                            (mOldDatabase[oldItemPosition].imageAltText == database[newItemPosition].imageAltText) &&
                            (mOldDatabase[oldItemPosition].tags == database[newItemPosition].tags) &&
                            (mOldDatabase[oldItemPosition].prevStripUrl == database[newItemPosition].prevStripUrl) &&
                            (mOldDatabase[oldItemPosition].nextStripUrl == database[newItemPosition].nextStripUrl))
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    areItemsTheSame(oldItemPosition, newItemPosition)
            }, false).dispatchUpdatesTo(mViewBinding.stripPager.adapter!!)

            mOldDatabase = database
        })

        mViewBinding.topAppBar.menu.findItem(R.id.action_next_strip).actionView = ImageButton(this).apply {
            background = null
            setImageResource(R.drawable.ic_round_keyboard_arrow_left_24)
            setOnClickListener {
                // no need for out-of-range check because already handled by pager
                mViewBinding.stripPager.currentItem -= 1
            }
            setOnLongClickListener {
                mViewBinding.stripPager.currentItem = 0
                return@setOnLongClickListener true
            }
        }

        mViewBinding.topAppBar.menu.findItem(R.id.action_prev_strip).actionView = ImageButton(this).apply {
            background = null
            setImageResource(R.drawable.ic_round_keyboard_arrow_right_24)
            setOnClickListener {
                // no need for out-of-range check because already handled by pager
                mViewBinding.stripPager.currentItem += 1
            }
            setOnLongClickListener {
                if (mViewBinding.stripPager.adapter == null) {
                    return@setOnLongClickListener true
                }
                mViewBinding.stripPager.currentItem = mViewBinding.stripPager.adapter!!.itemCount
                return@setOnLongClickListener true
            }
        }
    }

    override fun toggleIsFavourite(urlString: String) {
        mViewModel.toggleIsFavourite(urlString)
    }

    private lateinit var mViewBinding: ActivityComicBrowserBinding
    private val mViewModel: ComicBrowserViewModel by viewModels()
    private var mStripsFetched: Int = 0
    private var mOldDatabase: List<StripDataModel> = listOf()
}