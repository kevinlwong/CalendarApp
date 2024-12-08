package com.example.calendarapp

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.Calendar

class EventRepository(application: Application) {

    private val eventDao: EventDao = EventDatabase.getDatabase(application).eventDao()

    // Fetch events for a single day (start of the day to end of the day)
    fun getEventsByDate(date: Long): LiveData<List<Event>> {
        val startOfDay = normalizeDate(date)
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1 // End of the day (23:59:59)
        return eventDao.getEventsBetweenTimestamps(startOfDay, endOfDay)
    }

    // Fetch events within a date range
    fun getEventsByDateRange(start: Long, end: Long): LiveData<List<Event>> {
        return eventDao.getEventsBetweenTimestamps(start, end)
    }
    fun getAllEvents(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

    // Insert event into database
    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }

    suspend fun deleteEventById(eventId: Long) {
        eventDao.deleteById(eventId)
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