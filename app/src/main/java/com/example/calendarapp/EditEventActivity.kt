package com.example.calendarapp

import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar
import java.util.Locale

class EditEventActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var timeButton: Button
    private var selectedTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        timeButton = findViewById(R.id.btnSelectTime)

        // Populate fields with current event details
        val eventId = intent.getLongExtra("eventId", 0L)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val timestamp = intent.getLongExtra("timestamp", 0L)
        selectedTimeInMillis = intent.getLongExtra("timeInMillis", System.currentTimeMillis())

        titleEditText.setText(title)
        descriptionEditText.setText(description)
        updateTimeButtonText()

        timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        findViewById<Button>(R.id.btnSaveEvent).setOnClickListener {
            val updatedTitle = titleEditText.text.toString().trim()
            val updatedDescription = descriptionEditText.text.toString().trim()

            if (updatedTitle.isNotBlank()) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp
                    val timeCalendar = Calendar.getInstance().apply {
                        timeInMillis = selectedTimeInMillis
                    }
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                }

                val updatedEvent = Event(
                    id = eventId,
                    title = updatedTitle,
                    description = updatedDescription,
                    timestamp = timestamp, // or allow editing the timestamp
                    timeInMillis = calendar.timeInMillis
                )

                eventViewModel.updateEvent(updatedEvent)
                finish()
            } else {
                titleEditText.error = "Title cannot be empty"
            }
        }
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
