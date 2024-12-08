package com.example.calendarapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Get the selected date passed from MainActivity
        val selectedDate = intent.getLongExtra("selectedDate", System.currentTimeMillis())

        findViewById<Button>(R.id.btnSaveEvent).setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (title.isNotBlank() && description.isNotBlank()) {
                // Normalize the date to the start of the day
                val normalizedDate = normalizeDate(selectedDate)

                // Create a new event
                val newEvent = Event(
                    id = 0, // Auto-generated ID
                    title = title,
                    description = description,
                    timestamp = normalizedDate // Use timestamp instead of date
                )

                // Add the event through the ViewModel
                eventViewModel.addEvent(newEvent)

                // Finish the activity and return to MainActivity
                finish()
            } else {
                if (title.isBlank()) titleEditText.error = "Title cannot be empty"
                if (description.isBlank()) descriptionEditText.error = "Description cannot be empty"
            }
        }
    }

    // Normalize the timestamp to the start of the day (midnight)
    private fun normalizeDate(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}