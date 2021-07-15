package com.liweiyap.foxtrot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.util.StripDate
import java.text.DateFormatSymbols
import java.util.*

class StripViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.viewgroup_strip, parent, false)
) {
    private val stripTitle: TextView = itemView.findViewById(R.id.stripTitle)
    private val stripDate: TextView = itemView.findViewById(R.id.stripDate)
    private val stripImage: ImageView = itemView.findViewById(R.id.stripImage)

    fun bind(strip: StripDataModel) {
        stripTitle.text = strip.title
        stripDate.text = formatDate(strip.date)
        GlideApp.with(itemView).load(strip.imageSrc).defaultOptions().into(stripImage)
        stripImage.contentDescription = strip.imageAltText
    }

    private fun formatDate(date: StripDate): String {
        val dayOfWeek: (StripDate) -> String = { _date: StripDate ->
            DateFormatSymbols(Locale.ENGLISH).shortWeekdays[_date.getDayOfWeek()]
        }

        // https://stackoverflow.com/questions/1038570/how-can-i-convert-an-integer-to-localized-month-name-in-java
        val monthName: (StripDate) -> String = { _date: StripDate ->
            DateFormatSymbols(Locale.ENGLISH).shortMonths[_date.month - 1]
        }

        return dayOfWeek(date) + ", " + date.day + " " + monthName(date) + " " + date.year
    }
}