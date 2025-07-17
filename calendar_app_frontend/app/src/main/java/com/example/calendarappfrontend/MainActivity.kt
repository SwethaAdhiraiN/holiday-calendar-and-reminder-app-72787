package com.example.calendarappfrontend

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendarappfrontend.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

// PUBLIC_INTERFACE
/**
 * MainActivity displays the calendar, allows event and reminder management, and displays holidays.
 * Features:
 * - Monthly view with month/year selection
 * - FloatingActionButton for event entry
 * - List of reminders and holidays below calendar grid
 * - Minimalist, modern UI with provided color palette
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private var selectedDate: LocalDate = LocalDate.now()

    private val notificationChannelId = "reminder_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up App Bar with month/year
        updateAppBar(selectedDate)

        // Set up RecyclerViews
        val calendarAdapter = CalendarAdapter { date ->
            selectedDate = date
            updateAppBar(date)
            calendarViewModel.setSelectedDate(date)
        }
        binding.calendarGrid.adapter = calendarAdapter
        // Set up for 7 columns
        binding.calendarGrid.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 7)

        val eventAdapter = EventAdapter(
            onEdit = { event -> showEventDialog(event, isEditMode = true) },
            onDelete = { event -> calendarViewModel.deleteEvent(event) }
        )
        binding.eventList.adapter = eventAdapter
        binding.eventList.layoutManager = LinearLayoutManager(this)

        val holidayAdapter = HolidayAdapter()
        binding.holidayList.adapter = holidayAdapter
        binding.holidayList.layoutManager = LinearLayoutManager(this)

        // Observe data
        calendarViewModel.daysInMonth.observe(this, Observer {
            calendarAdapter.submitList(it)
        })
        calendarViewModel.eventsForSelectedDate.observe(this, Observer {
            eventAdapter.submitList(it)
            binding.eventList.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.emptyEvents.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
        calendarViewModel.holidaysForMonth.observe(this, Observer {
            holidayAdapter.submitList(it)
            binding.holidayList.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.emptyHolidays.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })

        // FAB for adding new event
        binding.fabAddEvent.setOnClickListener {
            showEventDialog(null, isEditMode = false)
        }

        // Next/Prev month
        binding.btnPrevMonth.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            calendarViewModel.goToMonth(selectedDate)
            updateAppBar(selectedDate)
        }
        binding.btnNextMonth.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            calendarViewModel.goToMonth(selectedDate)
            updateAppBar(selectedDate)
        }

        // Notification channel
        createNotificationChannel()
        // Listen for reminder notifications (simulate)
        calendarViewModel.reminderToNotify.observe(this, Observer { reminder ->
            if (reminder != null) {
                showNotification(reminder)
                calendarViewModel.clearReminderNotification()
            }
        })
    }

    private fun updateAppBar(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        binding.toolbar.title = date.format(formatter)
    }

    private fun showEventDialog(event: CalendarEvent?, isEditMode: Boolean) {
        val dialog = EventDialog(
            context = this,
            defaultDate = selectedDate,
            event = event,
            isEditMode = isEditMode,
            onSave = { evt ->
                if (isEditMode) {
                    calendarViewModel.updateEvent(evt)
                    Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show()
                } else {
                    calendarViewModel.addEvent(evt)
                    Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = {
                if (event != null) {
                    calendarViewModel.deleteEvent(event)
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
                }
            }
        )
        dialog.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminders"
            val descriptionText = "Reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(reminder: CalendarEvent) {
        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(reminder.title)
            .setContentText("Reminder: ${reminder.description}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            notify(reminder.id.hashCode(), builder.build())
        }
    }
}
