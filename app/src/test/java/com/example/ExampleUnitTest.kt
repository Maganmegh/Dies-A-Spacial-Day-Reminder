package com.example

import com.example.model.EventEntity
import com.example.model.Language
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
    @Test
    fun testCountdownAndAgeCalculations() {
        val today = LocalDate.of(2026, 10, 16)

        // Birthday today
        val daysToday = DateUtils.getDaysUntilNext(16, 10, today)
        assertEquals(0L, daysToday)
        assertTrue(DateUtils.isToday(16, 10, today))

        // Birthday tomorrow
        val daysTomorrow = DateUtils.getDaysUntilNext(17, 10, today)
        assertEquals(1L, daysTomorrow)
        assertTrue(DateUtils.isTomorrow(17, 10, today))

        // Birthday next month
        val daysNextMonth = DateUtils.getDaysUntilNext(16, 11, today)
        assertEquals(31L, daysNextMonth)

        // Age calculation for someone born in 2000
        val event = EventEntity(
            name = "Test User",
            dateDay = 16,
            dateMonth = 10,
            birthYear = 2000
        )
        val ageInfo = DateUtils.getAgeInfo(event, today)
        assertEquals(26, ageInfo?.turningAge)
    }

    @Test
    fun testBilingualMonthFormatting() {
        assertEquals("October", DateUtils.getMonthName(10, Language.ENGLISH))
        assertEquals("अक्टूबर", DateUtils.getMonthName(10, Language.HINDI))
    }

    @Test
    fun testEventAndPhotoEntities() {
        val event = com.example.model.Event(
            id = 1L,
            name = "Rohan",
            dateDay = 25,
            dateMonth = 12,
            birthYear = 1995,
            eventType = com.example.model.EventType.BIRTHDAY
        )
        val photo = com.example.model.Photo(
            id = 10L,
            eventId = 1L,
            uri = "file:///photos/photo_1.jpg",
            caption = "Birthday Cake Celebration",
            isAvatar = true
        )
        val eventWithPhotos = com.example.model.EventWithPhotos(
            event = event,
            photos = listOf(photo)
        )
        assertEquals("Rohan", eventWithPhotos.event.name)
        assertEquals(1, eventWithPhotos.photos.size)
        assertEquals("file:///photos/photo_1.jpg", eventWithPhotos.photos[0].uri)
        assertTrue(eventWithPhotos.photos[0].isAvatar)
    }
}
