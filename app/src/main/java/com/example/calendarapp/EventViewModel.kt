package com.example.calendarapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Calendar

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository: EventRepository

    init {
        // Initialize the repository with the Application context
        eventRepository = EventRepository(application)
    }

    // Fetch events for a specific date (start of day)
    fun getEventsByDate(date: Long): LiveData<List<Event>> {
        // Normalize the date to the start of the day (midnight)
        val startOfDay = normalizeDate(date)
        return eventRepository.getEventsByDate(startOfDay)
    }

    // Fetch events within a date range
    fun getEventsByDateRange(start: Long, end: Long): LiveData<List<Event>> {
        return eventRepository.getEventsByDateRange(start, end)
    }

    // Add a new event
    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.insert(event)
        }
    }

    fun getAllEvents(): LiveData<List<Event>> {
        return eventRepository.getAllEvents()
    }
    fun deleteEventById(eventId: Long) {
        viewModelScope.launch {
            eventRepository.deleteEventById(eventId)
        }
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
}