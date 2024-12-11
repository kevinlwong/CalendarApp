package com.example.calendarapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel
    private var selectedDate: Long = 0L // Variable to hold the currently selected date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the CalendarView and RecyclerView
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.eventRecyclerView)

        // Initialize Adapter with click and long-click listeners
        eventAdapter = EventAdapter(
            onItemClick = { event ->
                // Show due date as a Toast message when clicked
                val dueDate = formatTimestamp(event.timestamp)
                Toast.makeText(this, "Due: $dueDate", Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = { event ->
                // Show delete confirmation dialog when long-pressed
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
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventAdapter

        // Initialize ViewModel
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        // Initialize selectedDate with the currently visible date on the CalendarView
        selectedDate = normalizeDate(calendarView.date)

        // Fetch and observe events for the initially selected date
        eventViewModel.getEventsByDate(selectedDate).observe(this) { events ->
            Log.d("MainActivity", "Initial events for selected date: $events")
            eventAdapter.submitList(events)
        }

        // Set onDateChangeListener for CalendarView to load events for selected date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = getTimestamp(year, month, dayOfMonth)
            Log.d("MainActivity", "Selected Date: $selectedDate")

            // Fetch events for the selected date and observe LiveData
            eventViewModel.getEventsByDate(selectedDate).observe(this) { events ->
                Log.d("MainActivity", "Events for selected date range: $events")
                eventAdapter.submitList(events)
            }
        }

        // Add event button to navigate to AddEventActivity
        findViewById<View>(R.id.btnAddEvent).setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("selectedDate", selectedDate) // Pass selected date to AddEventActivity
            startActivity(intent)
        }

        // Initialize the BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Set the selected item to 'Calendar'
        bottomNavigation.selectedItemId = R.id.nav_calendar

        // Handle navigation item clicks
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendar -> true // Stay on the current screen
                R.id.nav_list -> {
                    val intent = Intent(this, ListViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    override fun onResume() {
        super.onResume()

        // Ensure the Calendar tab is selected when returning to this activity
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_calendar
    }

    private fun showDeleteConfirmationDialog(eventId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Yes") { _, _ ->
                // Delete the event
                eventViewModel.deleteEventById(eventId)
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun getTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0) // Set time to midnight to ignore hours/minutes
        calendar.set(Calendar.MILLISECOND, 0) // Set milliseconds to 0 to ensure accurate comparisons
        return calendar.timeInMillis
    }

    private fun normalizeDate(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun formatTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$month/$day/$year"
    }
}
