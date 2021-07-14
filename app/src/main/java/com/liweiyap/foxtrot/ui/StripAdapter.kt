package com.liweiyap.foxtrot.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liweiyap.foxtrot.database.StripDataModel

class StripAdapter(private var database: List<StripDataModel>): RecyclerView.Adapter<StripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StripViewHolder(parent)

    override fun onBindViewHolder(holder: StripViewHolder, position: Int) =
        holder.bind(database[position])

    override fun getItemCount() =
        database.size

    fun setData(newDataBase: List<StripDataModel>) {
        database = newDataBase
    }
}