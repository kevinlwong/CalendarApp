package com.example.calendarapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val onItemClick: (Event) -> Unit, // Listener for item clicks
    private val onItemLongClick: (Event) -> Unit // Listener for long-press actions
) : ListAdapter<Event, EventAdapter.EventViewHolder>(DiffCallback()) {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)

        fun bind(event: Event) {
            titleTextView.text = event.title
            descriptionTextView.text = event.description

            // Set click listener on the item
            itemView.setOnClickListener {
                onItemClick(event) // Pass the event to the click listener
            }

            // Set long-press listener on the item
            itemView.setOnLongClickListener {
                onItemLongClick(event) // Pass the event to the long-press listener
                true // Indicate that the long-press event is handled
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
}
