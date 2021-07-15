package com.liweiyap.foxtrot.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liweiyap.foxtrot.database.StripDataModel

/**
 * For thoughts about dependency injection, see:
 * http://frogermcs.github.io/inject-everything-viewholder-and-dagger-2-example/
 * https://stackoverflow.com/questions/63697582/how-to-inject-adapter-with-hilt
 */
class StripAdapter(private var database: List<StripDataModel>): RecyclerView.Adapter<StripHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StripHolder(parent)

    override fun onBindViewHolder(holder: StripHolder, position: Int) =
        holder.bind(database[position])

    override fun getItemCount() =
        database.size

    fun setData(newDataBase: List<StripDataModel>) {
        database = newDataBase
    }
}