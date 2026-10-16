package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.model.Event
import com.example.model.EventEntity
import com.example.model.EventWithPhotos
import com.example.model.MemoryEntity
import com.example.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY dateMonth ASC, dateDay ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY dateMonth ASC, dateDay ASC")
    suspend fun getAllEventsDirect(): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Long): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventByIdDirect(id: Long): EventEntity?

    @Transaction
    @Query("SELECT * FROM events ORDER BY dateMonth ASC, dateDay ASC")
    fun getEventsWithPhotos(): Flow<List<EventWithPhotos>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventWithPhotosById(id: Long): Flow<EventWithPhotos?>

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchEvents(query: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>): List<Long>

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("UPDATE events SET isWishedThisYear = :wished, wishedYear = :year WHERE id = :id")
    suspend fun setWishedStatus(id: Long, wished: Boolean, year: Int?)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventsCount(): Int
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE eventId = :eventId ORDER BY displayOrder ASC, createdAt DESC")
    fun getPhotosForEvent(eventId: Long): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE eventId = :eventId ORDER BY displayOrder ASC, createdAt DESC")
    suspend fun getPhotosForEventDirect(eventId: Long): List<Photo>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): Photo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<Photo>): List<Long>

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhotoById(id: Long)

    @Query("DELETE FROM photos WHERE eventId = :eventId")
    suspend fun deletePhotosByEventId(eventId: Long)

    @Query("SELECT COUNT(*) FROM photos WHERE eventId = :eventId")
    suspend fun getPhotosCountForEvent(eventId: Long): Int
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories WHERE eventId = :eventId ORDER BY year DESC, createdAt DESC")
    fun getMemoriesForEvent(eventId: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE eventId = :eventId ORDER BY year DESC")
    suspend fun getMemoriesForEventDirect(eventId: Long): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMemories(memories: List<MemoryEntity>)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM memories WHERE eventId = :eventId")
    suspend fun deleteMemoriesByEventId(eventId: Long)
}
