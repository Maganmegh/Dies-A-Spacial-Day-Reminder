package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.FirestoreEvent
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveEvent_authenticatedOwner_createsDocument() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val repository = FirestoreRepository(firestore)

    val event = FirestoreEvent(
      userId = aliceUid,
      name = "Alice Birthday",
      dateDay = 15,
      dateMonth = 8,
      eventType = "BIRTHDAY"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) { repository.saveEvent(event) }
    assertTrue(result.isSuccess)
    val eventId = result.getOrThrow()
    assertTrue(eventId.isNotEmpty())
  }

  @Test
  fun observeEvents_authenticatedOwner_returnsMatchingEvents() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val repository = FirestoreRepository(firestore)

    val event = FirestoreEvent(
      userId = aliceUid,
      name = "Alice Wedding",
      dateDay = 20,
      dateMonth = 11,
      eventType = "ANNIVERSARY"
    )

    val eventId = withTimeout(DEFAULT_TIMEOUT_MS) { repository.saveEvent(event).getOrThrow() }

    val emitted = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeEvents(aliceUid).first { list -> list.any { it.id == eventId } }
    }
    assertTrue(emitted.map { it.id }.contains(eventId))
  }

  @Test
  fun deleteEvent_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = FirestoreRepository(firestore)

    val event = FirestoreEvent(
      userId = aliceUid,
      name = "Private Celebration",
      dateDay = 1,
      dateMonth = 1,
      eventType = "SPECIAL_DAY"
    )
    val eventId = withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.saveEvent(event).getOrThrow() }

    // Switch to Bob
    val bobUid = signInTestUser(BOB_EMAIL)
    val bobRepo = FirestoreRepository(firestore)

    // Bob tries to delete Alice's event
    val result = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.deleteEvent(aliceUid, eventId) }
    assertTrue(result.isFailure)
  }

  @Test
  fun saveEvent_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = FirestoreRepository(firestore)

    val event = FirestoreEvent(
      userId = "unauthenticated_user_uid",
      name = "Unauthorized Event",
      dateDay = 1,
      dateMonth = 1,
      eventType = "BIRTHDAY"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) { repository.saveEvent(event) }
    assertTrue("Expected saveEvent to fail for unauthenticated user", result.isFailure)
    val exception = result.exceptionOrNull()
    assertTrue(exception is FirebaseFirestoreException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, (exception as FirebaseFirestoreException).code)
  }

  @Test
  fun getEvents_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = FirestoreRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.getEvents("unauthenticated_user_uid")
    }
    assertTrue("Expected getEvents to fail for unauthenticated user", result.isFailure)
    val exception = result.exceptionOrNull()
    assertTrue(exception is FirebaseFirestoreException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, (exception as FirebaseFirestoreException).code)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@test.com"
    const val BOB_EMAIL = "bob@test.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
