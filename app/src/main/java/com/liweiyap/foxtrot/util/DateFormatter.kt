package com.liweiyap.foxtrot.util

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import java.security.spec.InvalidParameterSpecException
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormatter {

    @JvmStatic fun formatDate(date: StripDate): String {
        val dayOfWeek: (StripDate) -> String = { _date: StripDate ->
            when (_date.getDayOfWeek()) {
                0 -> "Mon"
                1 -> "Tue"
                2 -> "Wed"
                3 -> "Thu"
                4 -> "Fri"
                5 -> "Sat"
                6 -> "Sun"
                else -> throw InvalidParameterSpecException()
            }
        }

        // https://stackoverflow.com/questions/1038570/how-can-i-convert-an-integer-to-localized-month-name-in-java
        val monthName: (StripDate) -> String = { _date: StripDate ->
            DateFormatSymbols(Locale.ENGLISH).shortMonths[_date.month - 1]
        }

        return dayOfWeek(date) + ", " + date.day + " " + monthName(date) + " " + date.year
    }

    @JvmStatic fun formatDate(rawDate: String): StripDate? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return formatDateSinceApi26(rawDate)
        }
        return formatDatePreApi26(rawDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic fun formatDateSinceApi26(rawDate: String): StripDate? {
        return try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(rawDate, formatter)
            StripDate(date.dayOfMonth, date.monthValue, date.year)
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic fun formatDatePreApi26(rawDate: String): StripDate? {
        return try {
            val format = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)
            val date: Date = format.parse(rawDate)
            val day: Int = Integer.parseInt(DateFormat.format("dd", date) as String)
            val month: Int = Integer.parseInt(DateFormat.format("M", date) as String)
            val year: Int = Integer.parseInt(DateFormat.format("yyyy", date) as String)
            StripDate(day, month, year)
        } catch (e: Exception) {
            null
        }
    }

    private const val DATE_PATTERN: String = "'Published' MMMM d, yyyy"
}