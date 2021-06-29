package com.liweiyap.foxtrot

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormatter {

    @JvmStatic fun formatDate(rawDate: String): StripDate? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return formatDateSinceApi26(rawDate)
        }
        return formatDatePreApi26(rawDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic fun formatDateSinceApi26(rawDate: String): StripDate? {
        return try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
            val date: LocalDate = LocalDate.parse(rawDate, formatter)
            StripDate(date.dayOfMonth, date.monthValue, date.year)
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic fun formatDatePreApi26(rawDate: String): StripDate? {
        return try {
            val format = SimpleDateFormat(datePattern, Locale.ENGLISH)
            val date: Date = format.parse(rawDate)
            val day: Int = Integer.parseInt(DateFormat.format("dd", date) as String)
            val month: Int = Integer.parseInt(DateFormat.format("M", date) as String)
            val year: Int = Integer.parseInt(DateFormat.format("yyyy", date) as String)
            StripDate(day, month, year)
        } catch (e: Exception) {
            null
        }
    }

    private const val datePattern: String = "'Published' MMMM d, yyyy"
}