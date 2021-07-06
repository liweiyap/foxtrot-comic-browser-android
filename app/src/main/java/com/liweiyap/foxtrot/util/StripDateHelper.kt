package com.liweiyap.foxtrot.util

import java.security.spec.InvalidParameterSpecException

object StripDateHelper {

    /**
     * The Gregorian calendar stipulates that if a year is evenly divisible by 100,
     * then it is a leap year only if it is also evenly divisible by 400.
     * Thus, 1900 is not a leap year.
     *  - https://docs.microsoft.com/en-us/office/troubleshoot/excel/determine-a-leap-year
     *  - https://kalender-365.de/leap-years.php
     */
    @JvmStatic fun isLeapYear(year: Int): Boolean {
        verifyYear(year)

        if (year % 4 != 0) {
            return false
        }

        if (year % 100 != 0) {
            return true
        }

        return (year % 400 == 0)
    }

    @JvmStatic fun countDaysInYear(year: Int): Int {
        verifyYear(year)

        if (isLeapYear(year)) {
            return 366
        }

        return 365
    }

    @JvmStatic fun countDaysInMonth(month: Int, year: Int): Int {
        verifyYear(year)
        verifyMonth(month)

        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> return 31
            4, 6, 9, 11 -> return 30
            2 -> {
                if (isLeapYear(year)) {
                    return 29
                }
                return 28
            }
        }

        throw RuntimeException("StripDateHelper::countDaysInMonth(): Error in retrieving no. of days in month $month of year $year.")
    }

    @JvmStatic fun isValidDate(date: StripDate): Boolean {
        try {
            verifyYear(date.year)
            verifyMonth(date.month)
            verifyDay(date.day, date.month, date.year)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    @JvmStatic fun countDaysFromReferenceDate(date: StripDate): Int {
        if (!isValidDate(date)) {
            throw InvalidParameterSpecException(
                "StripDateHelper::countDaysFromReferenceDate(): " +
                    "Invalid date (${date.day}/${date.month}/${date.year}) input.")
        }

        var day: Int = date.day
        var month: Int = date.month
        var year: Int = date.year

        var days = 0

        while (--day >= referenceDate.day) {
            ++days
        }

        while (--month >= referenceDate.month) {
            days += countDaysInMonth(month, year)
        }

        while (--year >= referenceDate.year) {
            days += countDaysInYear(year)
        }

        return days
    }

    @JvmStatic fun countDifferenceInDays(date0: StripDate, date1: StripDate): Int {
        val days0: Int = countDaysFromReferenceDate(date0)
        val days1: Int = countDaysFromReferenceDate(date1)
        return (days0 - days1)
    }

    private fun verifyYear(year: Int) {
        if (year < 1988) {
            throw InvalidParameterSpecException()
        }
    }

    private fun verifyMonth(month: Int) {
        if ( (month < 1) || (month > 12) ) {
            throw InvalidParameterSpecException()
        }
    }

    private fun verifyDay(day: Int, month: Int, year: Int) {
        if ( (day < 1) || (day > countDaysInMonth(month, year)) ) {
            throw InvalidParameterSpecException()
        }
    }

    @JvmStatic val referenceDate = StripDate(1, 1, 1988)
}