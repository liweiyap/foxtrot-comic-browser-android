package com.liweiyap.foxtrot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.util.StripDate
import java.text.DateFormatSymbols
import java.util.*

class StripFragment(private val strip: StripDataModel): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_strip, container, false)

    // https://stackoverflow.com/a/38718205/12367873
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stripTitle: TextView = view.findViewById(R.id.stripTitle)
        stripTitle.text = strip.title

        val stripDate: TextView = view.findViewById(R.id.stripDate)
        stripDate.text = formatDate(strip.date)
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