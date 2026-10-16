package com.example.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class EventType(val titleEn: String, val titleHi: String) {
    BIRTHDAY("Birthday", "जन्मदिन"),
    ANNIVERSARY("Anniversary", "सालगिरह"),
    SPECIAL_DAY("Special Day", "विशेष दिन"),
    OTHER("Custom Event", "अन्य अवसर")
}

enum class RelationshipType(val titleEn: String, val titleHi: String) {
    FRIEND("Friend", "मित्र"),
    FAMILY("Family", "परिवार"),
    WORK("Colleague", "सहकर्मी"),
    LOVE("Partner / Love", "जीवनसाथी / प्रेम"),
    OTHER("Acquaintance", "अन्य")
}

enum class Language {
    HINDI,
    ENGLISH
}

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dateDay: Int,
    val dateMonth: Int,
    val birthYear: Int? = null,
    val eventType: EventType = EventType.BIRTHDAY,
    val relationship: RelationshipType = RelationshipType.FRIEND,
    val notes: String = "",
    val avatarUri: String? = null,
    val photoUris: List<String> = emptyList(),
    val reminderDaysBefore: Int = 0, // 0 = day of event, 1 = 1 day before
    val reminderHour: Int = 9,      // 9:00 AM default
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val isWishedThisYear: Boolean = false,
    val wishedYear: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

typealias EventEntity = Event

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("eventId")]
)
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val uri: String,
    val caption: String = "",
    val isAvatar: Boolean = false,
    val displayOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class EventWithPhotos(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val photos: List<Photo>
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val year: Int,
    val title: String,
    val description: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class SpecialDayItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val day: Int,
    val month: Int,
    val categoryEn: String,
    val categoryHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val isReminderEnabled: Boolean = false
)

data class WishMessage(
    val id: String,
    val categoryEn: String,
    val categoryHi: String,
    val textEn: String,
    val textHi: String,
    val isFavorite: Boolean = false
)

data class BirthdaypackPayload(
    val app: String = "Dies",
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val totalRecords: Int,
    val records: List<BirthdaypackRecord>
)

data class BirthdaypackRecord(
    val name: String,
    val dateDay: Int,
    val dateMonth: Int,
    val birthYear: Int?,
    val eventType: String,
    val relationship: String,
    val notes: String,
    val reminderDaysBefore: Int,
    val reminderHour: Int,
    val reminderMinute: Int,
    val isReminderEnabled: Boolean,
    val avatarBase64: String? = null,
    val photosBase64: List<String> = emptyList(),
    val memories: List<BirthdaypackMemoryRecord> = emptyList()
)

data class BirthdaypackMemoryRecord(
    val year: Int,
    val title: String,
    val description: String,
    val photoBase64: String? = null
)
