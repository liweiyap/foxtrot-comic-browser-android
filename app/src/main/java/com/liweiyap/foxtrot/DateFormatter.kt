package com.liweiyap.foxtrot

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormatter {

    @JvmStatic fun formatDate(rawDate: String): StripDate {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return formatDateSinceApi26(rawDate)
        }
        return formatDatePreApi26(rawDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic fun formatDateSinceApi26(rawDate: String): StripDate {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
        val date: LocalDate = LocalDate.parse(rawDate, formatter)
        return StripDate(date.dayOfMonth, date.monthValue, date.year)
    }

    @JvmStatic fun formatDatePreApi26(rawDate: String): StripDate {
        val format = SimpleDateFormat(datePattern, Locale.ENGLISH)
        val date: Date? = format.parse(rawDate)
        val day: Int = Integer.parseInt(DateFormat.format("dd", date) as String)
        val month: Int = Integer.parseInt(DateFormat.format("M", date) as String)
        val year: Int = Integer.parseInt(DateFormat.format("yyyy", date) as String)
        return StripDate(day, month, year)
    }

    private const val datePattern: String = "'Published' MMMM d, yyyy"
}