package com.example.calendarappfrontend

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

// PUBLIC_INTERFACE
/**
 * Data class for calendar events and reminders.
 *
 * @param id Unique event ID
 * @param title Event/Reminder title
 * @param description Event/Reminder description
 * @param date LocalDate the event falls on
 * @param isReminder Whether it's a reminder (true) or an event (false)
 * @param reminderTime If reminder: the time to be reminded
 */
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var description: String,
    var date: LocalDate,
    var isReminder: Boolean = false,
    var reminderTime: LocalDateTime? = null
)
