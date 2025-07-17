package com.example.calendarappfrontend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarappfrontend.databinding.ItemEventBinding

// PUBLIC_INTERFACE
/**
 * Adapter for displaying list of events or reminders.
 */
class EventAdapter(
    private val onEdit: (CalendarEvent) -> Unit,
    private val onDelete: (CalendarEvent) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var items: List<CalendarEvent> = emptyList()

    fun submitList(list: List<CalendarEvent>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(evt: CalendarEvent) {
            binding.title.text = evt.title
            binding.description.text = evt.description
            binding.typeIcon.setImageResource(
                if (evt.isReminder) R.drawable.ic_reminder else R.drawable.ic_event
            )
            binding.editBtn.setOnClickListener { onEdit(evt) }
            binding.deleteBtn.setOnClickListener { onDelete(evt) }
        }
    }
}
