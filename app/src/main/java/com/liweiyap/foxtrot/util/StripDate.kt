package com.liweiyap.foxtrot.util

import java.security.spec.InvalidParameterSpecException

data class StripDate(val day: Int, val month: Int, val year: Int) {

    init {
        if (!StripDateHelper.isValidDate(this)) {
            throw InvalidParameterSpecException(
                "StripDateHelper::countDaysFromReferenceDate(): " +
                        "Invalid date ($day/$month/$year) input.")
        }
    }

    operator fun compareTo(other: StripDate): Int {
        return StripDateHelper.countDifferenceInDays(this, other)
    }

    operator fun minus(other: StripDate): Int {
        return StripDateHelper.countDifferenceInDays(this, other)
    }
}