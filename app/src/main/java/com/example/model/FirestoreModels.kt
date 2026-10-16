package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreEvent(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val dateDay: Int = 1,
    val dateMonth: Int = 1,
    val birthYear: Int? = null,
    val eventType: String = "BIRTHDAY",
    val relationship: String = "FRIEND",
    val notes: String = "",
    val isReminderEnabled: Boolean = true,
    val reminderDaysBefore: Int = 0,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val isWishedThisYear: Boolean = false,
    val wishedYear: Int? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreUserProfile(
    @DocumentId val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
