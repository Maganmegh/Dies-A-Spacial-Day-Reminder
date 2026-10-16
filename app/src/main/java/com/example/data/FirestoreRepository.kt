package com.example.data

import android.content.Context
import com.example.R
import com.example.model.FirestoreEvent
import com.example.model.FirestoreUserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observeEvents(userId: String): Flow<List<FirestoreEvent>> {
        val path = "users/$userId/events"
        return db.collection("users").document(userId).collection("events")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FirestoreEvent::class.java) }
    }

    suspend fun getEvents(userId: String): Result<List<FirestoreEvent>> {
        val path = "users/$userId/events"
        return try {
            val snapshot = db.collection("users").document(userId).collection("events").get().await()
            Result.success(snapshot.toObjects(FirestoreEvent::class.java))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun saveEvent(event: FirestoreEvent): Result<String> {
        require(event.userId.isNotBlank()) { "User ID must not be blank" }
        require(event.name.isNotBlank()) { "Event name must not be blank" }

        val collection = db.collection("users").document(event.userId).collection("events")
        val docRef = if (event.id.isNotBlank()) {
            collection.document(event.id)
        } else {
            collection.document()
        }

        return try {
            val payload = event.copy(id = docRef.id, updatedAt = null)
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(userId: String, eventId: String): Result<Unit> {
        val docRef = db.collection("users").document(userId).collection("events").document(eventId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(profile: FirestoreUserProfile): Result<Unit> {
        val docRef = db.collection("users").document(profile.userId)
        return try {
            docRef.set(profile.copy(updatedAt = null)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
