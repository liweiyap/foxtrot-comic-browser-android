package com.liweiyap.foxtrot.ui

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liweiyap.foxtrot.database.StripDataModel

class StripFragmentStateAdapter(
    @NonNull fragmentActivity: FragmentActivity,
    @NonNull private val database: List<StripDataModel>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = database.size

    override fun createFragment(position: Int): Fragment = StripFragment(database[position])
}