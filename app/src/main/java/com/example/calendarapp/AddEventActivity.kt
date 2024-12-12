package com.example.calendarapp

import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
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
    private lateinit var timeButton: Button
    private var selectedTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        timeButton = findViewById(R.id.btnSelectTime)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Get the selected date passed from MainActivity
        val selectedDate = intent.getLongExtra("selectedDate", System.currentTimeMillis())
        selectedTimeInMillis = System.currentTimeMillis()
        updateTimeButtonText()

        timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        findViewById<Button>(R.id.btnSaveEvent).setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (title.isNotBlank() && description.isNotBlank()) {
                // Normalize the date to the start of the day
                val normalizedDate = normalizeDate(selectedDate)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = normalizedDate
                    val timeCalendar = Calendar.getInstance().apply {
                        timeInMillis = selectedTimeInMillis
                    }
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                }

                // Create a new event
                val newEvent = Event(
                    id = 0, // Auto-generated ID
                    title = title,
                    description = description,
                    timestamp = normalizedDate, // Use timestamp instead of date
                    timeInMillis = calendar.timeInMillis
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

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedTimeInMillis

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTimeInMillis = calendar.timeInMillis
                updateTimeButtonText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateTimeButtonText() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedTimeInMillis
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        timeButton.text = "Time: ${timeFormat.format(calendar.time)}"
    }
}