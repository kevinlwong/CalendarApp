package com.example.calendarapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class EditEventActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Populate fields with current event details
        val eventId = intent.getLongExtra("eventId", 0L)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val timestamp = intent.getLongExtra("timestamp", 0L)

        titleEditText.setText(title)
        descriptionEditText.setText(description)

        findViewById<Button>(R.id.btnSaveEvent).setOnClickListener {
            val updatedTitle = titleEditText.text.toString().trim()
            val updatedDescription = descriptionEditText.text.toString().trim()

            if (updatedTitle.isNotBlank()) {
                val updatedEvent = Event(
                    id = eventId,
                    title = updatedTitle,
                    description = updatedDescription,
                    timestamp = timestamp // or allow editing the timestamp
                )

                eventViewModel.updateEvent(updatedEvent)
                finish()
            } else {
                titleEditText.error = "Title cannot be empty"
            }
        }
    }
}
