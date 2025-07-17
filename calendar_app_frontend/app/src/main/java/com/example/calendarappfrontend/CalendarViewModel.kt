package com.example.calendarappfrontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

// PUBLIC_INTERFACE
/**
 * ViewModel for calendar data, event/reminder state and holidays.
 */
class CalendarViewModel : ViewModel() {
    private val _daysInMonth = MutableLiveData<List<CalendarDay>>()
    val daysInMonth: LiveData<List<CalendarDay>> = _daysInMonth

    private val _eventsForSelectedDate = MutableLiveData<List<CalendarEvent>>()
    val eventsForSelectedDate: LiveData<List<CalendarEvent>> = _eventsForSelectedDate

    private val _holidaysForMonth = MutableLiveData<List<Holiday>>()
    val holidaysForMonth: LiveData<List<Holiday>> = _holidaysForMonth

    private val _reminderToNotify = MutableLiveData<CalendarEvent?>()
    val reminderToNotify: LiveData<CalendarEvent?> = _reminderToNotify

    private val events = mutableListOf<CalendarEvent>()
    private var currentMonth: YearMonth = YearMonth.now()
    private var selectedDate: LocalDate = LocalDate.now()

    private val holidaysService = HolidaysService()

    init {
        setSelectedDate(selectedDate)
    }

    // PUBLIC_INTERFACE
    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        currentMonth = YearMonth.from(date)
        updateDays()
        updateEvents()
        updateHolidays()
    }

    // PUBLIC_INTERFACE
    fun goToMonth(date: LocalDate) {
        currentMonth = YearMonth.from(date)
        selectedDate = date.withDayOfMonth(1)
        updateDays()
        updateHolidays()
        updateEvents()
    }

    private fun updateDays() {
        val days = mutableListOf<CalendarDay>()
        val firstOfMonth = currentMonth.atDay(1)
        val startDayOfWeek = (firstOfMonth.dayOfWeek.value % 7) // Sunday = 0
        repeat(startDayOfWeek) { days.add(CalendarDay(null)) }
        for (day in 1..currentMonth.lengthOfMonth()) {
            val date = currentMonth.atDay(day)
            val dayEvents = events.filter { it.date == date }
            val holiday = holidaysService.getHolidayForDate(date)
            days.add(CalendarDay(date, dayEvents, holiday))
        }
        _daysInMonth.value = days
    }

    private fun updateEvents() {
        _eventsForSelectedDate.value = events.filter { it.date == selectedDate }
    }

    private fun updateHolidays() {
        _holidaysForMonth.value = holidaysService.getHolidaysForMonth(currentMonth)
    }

    // PUBLIC_INTERFACE
    fun addEvent(event: CalendarEvent) {
        events.add(event)
        updateDays()
        updateEvents()
        if (event.isReminder && event.reminderTime != null &&
            event.reminderTime!!.toLocalDate() == LocalDate.now()
        ) {
            // Simulate instant reminder notification for demo
            _reminderToNotify.value = event
        }
    }

    // PUBLIC_INTERFACE
    fun updateEvent(newEvent: CalendarEvent) {
        events.replaceAll { if (it.id == newEvent.id) newEvent else it }
        updateDays()
        updateEvents()
    }

    // PUBLIC_INTERFACE
    fun deleteEvent(event: CalendarEvent) {
        events.removeAll { it.id == event.id }
        updateDays()
        updateEvents()
    }

    // PUBLIC_INTERFACE
    fun clearReminderNotification() {
        _reminderToNotify.value = null
    }
}

// Wrapper for cells in the calendar grid
data class CalendarDay(
    val date: LocalDate?,
    val events: List<CalendarEvent> = emptyList(),
    val holiday: Holiday? = null
)
