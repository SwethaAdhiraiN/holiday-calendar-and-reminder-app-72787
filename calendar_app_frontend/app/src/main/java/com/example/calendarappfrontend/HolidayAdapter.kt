package com.example.calendarappfrontend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarappfrontend.databinding.ItemHolidayBinding

// PUBLIC_INTERFACE
/**
 * Adapter for holidays list.
 */
class HolidayAdapter : RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder>() {
    private var holidays: List<Holiday> = emptyList()

    fun submitList(list: List<Holiday>) {
        holidays = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val binding = ItemHolidayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HolidayViewHolder(binding)
    }

    override fun getItemCount(): Int = holidays.size

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        holder.bind(holidays[position])
    }

    class HolidayViewHolder(private val binding: ItemHolidayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holiday: Holiday) {
            binding.holidayName.text = holiday.name
            binding.holidayDate.text = holiday.date.toString()
        }
    }
}
