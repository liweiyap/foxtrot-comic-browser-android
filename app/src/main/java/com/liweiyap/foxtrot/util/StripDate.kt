package com.liweiyap.foxtrot.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.security.spec.InvalidParameterSpecException

@Parcelize
data class StripDate(val day: Int, val month: Int, val year: Int): Parcelable {

    init {
        if (!StripDateHelper.isValidDate(this)) {
            throw InvalidParameterSpecException(
                "StripDateHelper::countDaysFromReferenceDate(): " +
                        "Invalid date ($day/$month/$year) input.")
        }
    }

    operator fun compareTo(other: StripDate): Int =
        StripDateHelper.countDifferenceInDays(this, other)

    operator fun minus(other: StripDate): Int =
        StripDateHelper.countDifferenceInDays(this, other)

    fun getDayOfWeek(): Int =
        StripDateHelper.getDayOfWeek(this)
}