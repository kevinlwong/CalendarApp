package com.example.calendarapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel

    private lateinit var detailCard: View
    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailDueDate: TextView
    private lateinit var closeButton: View

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.eventRecyclerView)

        // Initialize Detail Card
        detailCard = findViewById(R.id.detailCard)
        detailTitle = findViewById(R.id.detailTitle)
        detailDescription = findViewById(R.id.detailDescription)
        detailDueDate = findViewById(R.id.detailDueDate)
        closeButton = findViewById(R.id.btnCloseDetail)

        // Set up close button for the detailed card
        closeButton.setOnClickListener {
            detailCard.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        eventAdapter = EventAdapter(
            onItemClick = { event ->
                // Show detailed card
                detailTitle.text = event.title
                detailDescription.text = event.description
                detailDueDate.text = "Due: ${formatTimestamp(event.timestamp)}"

                detailCard.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            },
            onItemLongClick = { event ->
                showDeleteConfirmationDialog(event.id)
            },
            onEditClick = { event ->
                // Navigate to EditEventActivity or show an edit dialog
                val intent = Intent(this, EditEventActivity::class.java)
                intent.putExtra("eventId", event.id)
                intent.putExtra("title", event.title)
                intent.putExtra("description", event.description)
                intent.putExtra("timestamp", event.timestamp)
                startActivity(intent)
            },
            onUpdateCompletion = { event ->
                eventViewModel.updateEventCompletion(event.id, event.isCompleted)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventAdapter

        // Initialize ViewModel
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Observe and display all events
        eventViewModel.getAllEvents().observe(this) { events ->
            eventAdapter.submitList(events)
        }

        // Initialize BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_list
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendar -> {
                    finish() // Navigate back to Calendar
                    true
                }
                R.id.nav_list -> true // Stay on this screen
                else -> false
            }
        }
    }

    private fun showDeleteConfirmationDialog(eventId: Long) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Yes") { _, _ ->
                eventViewModel.deleteEventById(eventId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return "$month/$day/$year"
    }
}