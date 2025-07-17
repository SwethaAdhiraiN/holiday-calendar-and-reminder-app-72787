package com.example.calendarappfrontend

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.calendarappfrontend.databinding.DialogEventBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// PUBLIC_INTERFACE
/**
 * Dialog for event/reminder entry and editing.
 */
class EventDialog(
    context: Context,
    defaultDate: LocalDate,
    event: CalendarEvent?,
    isEditMode: Boolean,
    private val onSave: (CalendarEvent) -> Unit,
    private val onDelete: () -> Unit
) : Dialog(context) {

    private val binding = DialogEventBinding.inflate(LayoutInflater.from(context))
    private var eventDate: LocalDate = event?.date ?: defaultDate
    private var isReminder: Boolean = event?.isReminder ?: false
    private var reminderTime: LocalDateTime? = event?.reminderTime

    init {
        setContentView(binding.root)
        binding.inputTitle.setText(event?.title ?: "")
        binding.inputDesc.setText(event?.description ?: "")
        binding.dateBtn.text = eventDate.toString()
        binding.reminderSwitch.isChecked = isReminder

        binding.reminderTimeBtn.isEnabled = isReminder
        binding.reminderTimeBtn.text = reminderTime?.toLocalTime()?.toString() ?: "--:--"
        binding.reminderSwitch.setOnCheckedChangeListener { _, checked ->
            isReminder = checked
            binding.reminderTimeBtn.isEnabled = checked
        }

        binding.dateBtn.setOnClickListener {
            val datePicker = DatePickerDialog(context, { _, y, m, d ->
                eventDate = LocalDate.of(y, m + 1, d)
                binding.dateBtn.text = eventDate.toString()
            }, eventDate.year, eventDate.monthValue - 1, eventDate.dayOfMonth)
            datePicker.show()
        }

        binding.reminderTimeBtn.setOnClickListener {
            val time = reminderTime?.toLocalTime() ?: LocalTime.now()
            val picker = TimePickerDialog(context, { _, hour, min ->
                reminderTime = LocalDateTime.of(eventDate, LocalTime.of(hour, min))
                binding.reminderTimeBtn.text = reminderTime!!.toLocalTime().toString()
            }, time.hour, time.minute, true)
            picker.show()
        }

        binding.saveBtn.setOnClickListener {
            val title = binding.inputTitle.text.toString().trim()
            val desc = binding.inputDesc.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(context, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val evt = CalendarEvent(
                id = event?.id ?: "",
                title = title,
                description = desc,
                date = eventDate,
                isReminder = isReminder,
                reminderTime = if (isReminder) reminderTime else null
            )
            onSave(evt)
            dismiss()
        }

        binding.deleteBtn.visibility = if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
        binding.deleteBtn.setOnClickListener {
            onDelete()
            dismiss()
        }

        binding.cancelBtn.setOnClickListener { dismiss() }
    }
}
