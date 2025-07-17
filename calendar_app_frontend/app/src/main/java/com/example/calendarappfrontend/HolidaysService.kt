package com.example.calendarappfrontend

import java.time.LocalDate
import java.time.YearMonth

// PUBLIC_INTERFACE
/**
 * Very simple local holidays service — in real case would fetch from a server or DB.
 */
class HolidaysService {
    private val staticHolidays = listOf(
        Holiday("New Year's Day", LocalDate.of(LocalDate.now().year, 1, 1)),
        Holiday("Independence Day", LocalDate.of(LocalDate.now().year, 7, 4)),
        Holiday("Christmas", LocalDate.of(LocalDate.now().year, 12, 25)),
        // Add more holidays here as needed
    )

    fun getHolidaysForMonth(month: YearMonth): List<Holiday> {
        return staticHolidays.filter { it.date.month == month.month }
    }

    fun getHolidayForDate(date: LocalDate): Holiday? {
        return staticHolidays.find { it.date == date }
    }
}

// PUBLIC_INTERFACE
/**
 * Data model for holidays.
 */
data class Holiday(
    val name: String,
    val date: LocalDate
)
