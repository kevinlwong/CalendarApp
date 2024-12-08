package com.example.calendarapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Retrieve the event details passed from the intent
        val title = intent.getStringExtra("title") ?: "No Title"
        val description = intent.getStringExtra("description") ?: "No Description"
        val timestamp = intent.getLongExtra("timestamp", 0L)

        // Format the timestamp into a human-readable date
        val dueDate = formatTimestamp(timestamp)

        // Display the event details
        findViewById<TextView>(R.id.textTitle).text = title
        findViewById<TextView>(R.id.textDescription).text = description
        findViewById<TextView>(R.id.textDueDate).text = "Due: $dueDate"
    }

    // Helper function to format timestamp into a readable date
    private fun formatTimestamp(timestamp: Long): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // Month is zero-based
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return "$month/$day/$year"
    }
}
