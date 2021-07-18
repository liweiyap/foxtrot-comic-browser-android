package com.liweiyap.foxtrot.ui

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel

/**
 * FragmentStateAdapter already extends RecyclerView.Adapter
 * When we use RecyclerView.Adapter in lieu of FragmentStateAdapter and RecyclerView.ViewHolder in lieu of Fragment,
 * we might accidentally turn on the visibility of some View like a reload button on a different ViewHolder in a StripGlideRequestListenerCallback.
 * This might be because ViewPager2::offScreenPageLimit is always > 0, so some pages in the ViewPager are loaded in advance,
 * causing the ReloadMaterialButton on the wrong page to also show up.
 *
 * For thoughts about dependency injection, see:
 * http://frogermcs.github.io/inject-everything-viewholder-and-dagger-2-example/
 * https://stackoverflow.com/questions/63697582/how-to-inject-adapter-with-hilt
 */
class StripFragmentStateAdapter(
    @NonNull private val fragmentActivity: FragmentActivity,
    @NonNull private var database: List<StripDataModel>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int =
        database.size

    override fun createFragment(position: Int): Fragment {
        val stripFragment = StripFragment()

        val args = Bundle()
        args.putParcelable(fragmentActivity.resources.getString(R.string.stripDataModel_fragmentParcelable), database[position])  // https://stackoverflow.com/a/5784291/12367873
        stripFragment.arguments = args

        return stripFragment
    }

    fun setData(newDataBase: List<StripDataModel>) {
        database = newDataBase
    }
}