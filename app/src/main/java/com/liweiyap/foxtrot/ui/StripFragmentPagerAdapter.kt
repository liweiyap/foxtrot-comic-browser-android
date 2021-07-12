package com.liweiyap.foxtrot.ui

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liweiyap.foxtrot.database.StripDataModel

class StripFragmentPagerAdapter(
    @NonNull fragmentManager: FragmentManager,
    @NonNull lifecycle: Lifecycle,
    @NonNull private val database: List<StripDataModel>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = database.size

    override fun createFragment(position: Int): Fragment = StripFragment(database[position])
}