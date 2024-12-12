package com.example.calendarapp

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Locale
import android.widget.CheckBox

class EventAdapter(
    private val onItemClick: (Event) -> Unit, // Listener for item clicks
    private val onItemLongClick: (Event) -> Unit, // Listener for long-press actions
    private val onEditClick: (Event) -> Unit, // Listener for edit actions
    private val onUpdateCompletion: (Event) -> Unit // Listener for completed task
) : ListAdapter<Event, EventAdapter.EventViewHolder>(DiffCallback()) {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        private val timeTextView: TextView = itemView.findViewById(R.id.time)
        private val editButton: Button = itemView.findViewById(R.id.btnEdit) // Add reference to the Edit button
        private val checkboxComplete: CheckBox = itemView.findViewById(R.id.checkboxComplete)

        fun bind(event: Event) {
            titleTextView.text = event.title
            descriptionTextView.text = event.description
            timeTextView.text = formatTime(event.timeInMillis)

            //gray-out task
            if (event.isCompleted) {
                itemView.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                checkboxComplete.isChecked = true
            } else {
                itemView.setBackgroundColor(itemView.context.getColor(android.R.color.white))
                checkboxComplete.isChecked = false
            }

            // checkbox  clicks
            checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                event.isCompleted = isChecked
                onUpdateCompletion(event)

                if (isChecked) {
                    itemView.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                } else {
                    itemView.setBackgroundColor(itemView.context.getColor(android.R.color.white))
                }
            }

            // Set click listener on the item
            itemView.setOnClickListener {
                onItemClick(event) // Pass the event to the click listener
            }

            // Set long-press listener on the item
            itemView.setOnLongClickListener {
                onItemLongClick(event) // Pass the event to the long-press listener
                true // Indicate that the long-press event is handled
            }

            // Set click listener for the Edit button
            editButton.setOnClickListener {
                onEditClick(event)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    private fun formatTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}
