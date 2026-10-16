package com.example.data

import com.example.model.Event
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.EventWithPhotos
import com.example.model.MemoryEntity
import com.example.model.Photo
import com.example.model.RelationshipType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class EventRepository(
    private val eventDao: EventDao,
    private val photoDao: PhotoDao,
    private val memoryDao: MemoryDao
) {
    val allEvents: Flow<List<EventEntity>> = eventDao.getAllEvents()

    val eventsWithPhotos: Flow<List<EventWithPhotos>> = eventDao.getEventsWithPhotos()

    suspend fun getAllEventsDirect(): List<EventEntity> = eventDao.getAllEventsDirect()

    fun getEventById(id: Long): Flow<EventEntity?> = eventDao.getEventById(id)

    suspend fun getEventByIdDirect(id: Long): EventEntity? = eventDao.getEventByIdDirect(id)

    fun getEventWithPhotosById(id: Long): Flow<EventWithPhotos?> = eventDao.getEventWithPhotosById(id)

    fun searchEvents(query: String): Flow<List<EventEntity>> = eventDao.searchEvents(query)

    suspend fun insertEvent(event: EventEntity): Long = eventDao.insertEvent(event)

    suspend fun insertAll(events: List<EventEntity>): List<Long> = eventDao.insertAll(events)

    suspend fun updateEvent(event: EventEntity) = eventDao.updateEvent(event)

    suspend fun deleteEvent(id: Long) {
        photoDao.deletePhotosByEventId(id)
        memoryDao.deleteMemoriesByEventId(id)
        eventDao.deleteEventById(id)
    }

    suspend fun setWishedStatus(id: Long, wished: Boolean, year: Int?) {
        eventDao.setWishedStatus(id, wished, year)
    }

    // Photo Operations
    fun getPhotosForEvent(eventId: Long): Flow<List<Photo>> = photoDao.getPhotosForEvent(eventId)

    suspend fun getPhotosForEventDirect(eventId: Long): List<Photo> = photoDao.getPhotosForEventDirect(eventId)

    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)

    suspend fun insertPhotos(photos: List<Photo>): List<Long> = photoDao.insertPhotos(photos)

    suspend fun updatePhoto(photo: Photo) = photoDao.updatePhoto(photo)

    suspend fun deletePhoto(id: Long) = photoDao.deletePhotoById(id)

    // Memories
    fun getMemoriesForEvent(eventId: Long): Flow<List<MemoryEntity>> =
        memoryDao.getMemoriesForEvent(eventId)

    suspend fun getMemoriesForEventDirect(eventId: Long): List<MemoryEntity> =
        memoryDao.getMemoriesForEventDirect(eventId)

    suspend fun insertMemory(memory: MemoryEntity): Long = memoryDao.insertMemory(memory)

    suspend fun deleteMemory(id: Long) = memoryDao.deleteMemoryById(id)

    // Initial starter seed data if empty
    suspend fun seedInitialDataIfEmpty() {
        if (eventDao.getEventsCount() == 0) {
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val nextWeek = today.plusDays(7)

            val starterEvents = listOf(
                Event(
                    name = "Aarav Sharma",
                    dateDay = today.dayOfMonth,
                    dateMonth = today.monthValue,
                    birthYear = today.year - 24,
                    eventType = EventType.BIRTHDAY,
                    relationship = RelationshipType.FRIEND,
                    notes = "Best friend from college. Loves astronomy and chai. Always plans road trips!",
                    reminderDaysBefore = 0,
                    reminderHour = 9,
                    reminderMinute = 0,
                    isReminderEnabled = true
                ),
                Event(
                    name = "Maa (Mother)",
                    dateDay = tomorrow.dayOfMonth,
                    dateMonth = tomorrow.monthValue,
                    birthYear = today.year - 52,
                    eventType = EventType.BIRTHDAY,
                    relationship = RelationshipType.FAMILY,
                    notes = "Order fresh flowers and make sure to call early in the morning!",
                    reminderDaysBefore = 1,
                    reminderHour = 8,
                    reminderMinute = 30,
                    isReminderEnabled = true
                ),
                Event(
                    name = "Priya & Rahul",
                    dateDay = nextWeek.dayOfMonth,
                    dateMonth = nextWeek.monthValue,
                    birthYear = today.year - 3,
                    eventType = EventType.ANNIVERSARY,
                    relationship = RelationshipType.FAMILY,
                    notes = "3rd Wedding Anniversary. Send personalized congratulations card.",
                    reminderDaysBefore = 1,
                    reminderHour = 10,
                    reminderMinute = 0,
                    isReminderEnabled = true
                )
            )

            for (e in starterEvents) {
                val eventId = eventDao.insertEvent(e)
                if (e.name == "Aarav Sharma") {
                    memoryDao.insertMemory(
                        MemoryEntity(
                            eventId = eventId,
                            year = today.year - 1,
                            title = "Manali Trek & Star Gazing",
                            description = "Celebrated 23rd birthday around a bonfire in the Himalayas."
                        )
                    )
                }
            }
        }
    }
}
