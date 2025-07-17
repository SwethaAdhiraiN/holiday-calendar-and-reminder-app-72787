package com.example.calendarappfrontend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarappfrontend.databinding.ItemCalendarDayBinding
import java.time.LocalDate

// PUBLIC_INTERFACE
/**
 * Adapter for the calendar grid displaying days.
 */
class CalendarAdapter(
    private val onDayClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var days = listOf<CalendarDay>()

    fun submitList(items: List<CalendarDay>) {
        days = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    inner class DayViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            with(binding) {
                if (day.date == null) {
                    dayCell.text = ""
                    dayCell.setBackgroundColor(Color.TRANSPARENT)
                    eventDot.visibility = View.INVISIBLE
                    holidayDot.visibility = View.INVISIBLE
                    root.setOnClickListener(null)
                } else {
                    dayCell.text = day.date.dayOfMonth.toString()
                    dayCell.setBackgroundResource(
                        if (day.date == LocalDate.now()) R.drawable.bg_accent_circle
                        else android.R.color.transparent
                    )
                    eventDot.visibility = if (day.events.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    holidayDot.visibility = if (day.holiday != null) View.VISIBLE else View.INVISIBLE
                    root.setOnClickListener { onDayClick(day.date) }
                }
            }
        }
    }
}
