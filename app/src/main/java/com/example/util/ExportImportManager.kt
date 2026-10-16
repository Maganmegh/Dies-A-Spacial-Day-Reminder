package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.model.BirthdaypackMemoryRecord
import com.example.model.BirthdaypackPayload
import com.example.model.BirthdaypackRecord
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.MemoryEntity
import com.example.model.RelationshipType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

enum class DuplicateStrategy {
    SKIP_DUPLICATES,      // Ignore records matching existing name & birthday
    OVERWRITE_EXISTING,   // Update existing matching records
    ADD_ALL_AS_NEW        // Add all without checking
}

data class ImportPreviewResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val payload: BirthdaypackPayload? = null,
    val totalInFile: Int = 0,
    val newCount: Int = 0,
    val duplicateCount: Int = 0,
    val sampleNames: List<String> = emptyList()
)

object ExportImportManager {

    suspend fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val photosDir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
            val fileName = "photo_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(photosDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destFile).toString()
        } catch (_: Exception) {
            null
        }
    }

    private fun fileToBase64(uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            val file = if (uri.scheme == "file") File(uri.path ?: "") else null
            if (file != null && file.exists()) {
                // Downsample image for export efficiency if large
                val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
                val scaled = if (bitmap.width > 600 || bitmap.height > 600) {
                    val ratio = 600f / maxOf(bitmap.width, bitmap.height)
                    Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
                } else {
                    bitmap
                }
                val out = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 75, out)
                Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun base64ToFile(context: Context, base64: String?): String? {
        if (base64.isNullOrBlank()) return null
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val photosDir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
            val fileName = "imported_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(photosDir, fileName)
            FileOutputStream(destFile).use { it.write(bytes) }
            Uri.fromFile(destFile).toString()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun exportEvents(
        context: Context,
        events: List<EventEntity>,
        memoriesProvider: suspend (Long) -> List<MemoryEntity>,
        includePhotos: Boolean = false
    ): Pair<File, Uri> = withContext(Dispatchers.IO) {
        val rootJson = JSONObject()
        rootJson.put("app", "Dies")
        rootJson.put("format", "birthdaypack")
        rootJson.put("version", 1)
        rootJson.put("exportedAt", System.currentTimeMillis())
        rootJson.put("includePhotos", includePhotos)

        val recordsArray = JSONArray()
        for (event in events) {
            val recJson = JSONObject()
            recJson.put("name", event.name)
            recJson.put("dateDay", event.dateDay)
            recJson.put("dateMonth", event.dateMonth)
            if (event.birthYear != null) recJson.put("birthYear", event.birthYear)
            recJson.put("eventType", event.eventType.name)
            recJson.put("relationship", event.relationship.name)
            recJson.put("notes", event.notes)
            recJson.put("reminderDaysBefore", event.reminderDaysBefore)
            recJson.put("reminderHour", event.reminderHour)
            recJson.put("reminderMinute", event.reminderMinute)
            recJson.put("isReminderEnabled", event.isReminderEnabled)

            if (includePhotos) {
                event.avatarUri?.let { uri ->
                    fileToBase64(uri)?.let { recJson.put("avatarBase64", it) }
                }
                val photosArr = JSONArray()
                event.photoUris.forEach { uri ->
                    fileToBase64(uri)?.let { photosArr.put(it) }
                }
                recJson.put("photosBase64", photosArr)

                val eventMemories = memoriesProvider(event.id)
                val memArr = JSONArray()
                for (mem in eventMemories) {
                    val mJson = JSONObject()
                    mJson.put("year", mem.year)
                    mJson.put("title", mem.title)
                    mJson.put("description", mem.description)
                    mem.photoUri?.let { pUri ->
                        fileToBase64(pUri)?.let { mJson.put("photoBase64", it) }
                    }
                    memArr.put(mJson)
                }
                recJson.put("memories", memArr)
            }
            recordsArray.put(recJson)
        }
        rootJson.put("records", recordsArray)

        // Write to cache exports directory
        val exportDir = File(context.cacheDir, "exports").apply { if (!exists()) mkdirs() }
        val fileName = "dies_backup_${System.currentTimeMillis()}.birthdaypack"
        val exportFile = File(exportDir, fileName)
        FileOutputStream(exportFile).use { it.write(rootJson.toString(2).toByteArray(Charsets.UTF_8)) }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            exportFile
        )
        Pair(exportFile, contentUri)
    }

    suspend fun parseAndPreviewImport(
        context: Context,
        fileUri: Uri,
        existingEvents: List<EventEntity>
    ): ImportPreviewResult = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(fileUri)?.use { input ->
                input.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext ImportPreviewResult(false, "Cannot read file")

            val rootJson = JSONObject(jsonString)
            if (!rootJson.has("records")) {
                return@withContext ImportPreviewResult(false, "Invalid .birthdaypack file format")
            }

            val recordsArray = rootJson.getJSONArray("records")
            val recordsList = mutableListOf<BirthdaypackRecord>()
            val sampleNames = mutableListOf<String>()

            for (i in 0 until recordsArray.length()) {
                val r = recordsArray.getJSONObject(i)
                val name = r.getString("name")
                if (sampleNames.size < 5) sampleNames.add(name)

                val memList = mutableListOf<BirthdaypackMemoryRecord>()
                if (r.has("memories")) {
                    val mArr = r.getJSONArray("memories")
                    for (j in 0 until mArr.length()) {
                        val m = mArr.getJSONObject(j)
                        memList.add(
                            BirthdaypackMemoryRecord(
                                year = m.optInt("year", 2026),
                                title = m.optString("title", ""),
                                description = m.optString("description", ""),
                                photoBase64 = m.optString("photoBase64", null)
                            )
                        )
                    }
                }

                val photosBase64List = mutableListOf<String>()
                if (r.has("photosBase64")) {
                    val pArr = r.getJSONArray("photosBase64")
                    for (k in 0 until pArr.length()) {
                        photosBase64List.add(pArr.getString(k))
                    }
                }

                recordsList.add(
                    BirthdaypackRecord(
                        name = name,
                        dateDay = r.getInt("dateDay"),
                        dateMonth = r.getInt("dateMonth"),
                        birthYear = if (r.has("birthYear")) r.getInt("birthYear") else null,
                        eventType = r.optString("eventType", "BIRTHDAY"),
                        relationship = r.optString("relationship", "FRIEND"),
                        notes = r.optString("notes", ""),
                        reminderDaysBefore = r.optInt("reminderDaysBefore", 0),
                        reminderHour = r.optInt("reminderHour", 9),
                        reminderMinute = r.optInt("reminderMinute", 0),
                        isReminderEnabled = r.optBoolean("isReminderEnabled", true),
                        avatarBase64 = r.optString("avatarBase64", null),
                        photosBase64 = photosBase64List,
                        memories = memList
                    )
                )
            }

            var duplicateCount = 0
            for (rec in recordsList) {
                val isDup = existingEvents.any { existing ->
                    existing.name.trim().equals(rec.name.trim(), ignoreCase = true) &&
                            existing.dateDay == rec.dateDay &&
                            existing.dateMonth == rec.dateMonth
                }
                if (isDup) duplicateCount++
            }

            val payload = BirthdaypackPayload(
                app = rootJson.optString("app", "Dies"),
                version = rootJson.optInt("version", 1),
                exportedAt = rootJson.optLong("exportedAt", System.currentTimeMillis()),
                totalRecords = recordsList.size,
                records = recordsList
            )

            ImportPreviewResult(
                isValid = true,
                payload = payload,
                totalInFile = recordsList.size,
                newCount = recordsList.size - duplicateCount,
                duplicateCount = duplicateCount,
                sampleNames = sampleNames
            )
        } catch (e: Exception) {
            ImportPreviewResult(false, e.localizedMessage ?: "File parsing error")
        }
    }

    suspend fun executeImport(
        context: Context,
        payload: BirthdaypackPayload,
        strategy: DuplicateStrategy,
        existingEvents: List<EventEntity>,
        onInsertEvent: suspend (EventEntity) -> Long,
        onUpdateEvent: suspend (EventEntity) -> Unit,
        onInsertMemory: suspend (MemoryEntity) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        var importedCount = 0

        for (rec in payload.records) {
            val matchingExisting = existingEvents.find { existing ->
                existing.name.trim().equals(rec.name.trim(), ignoreCase = true) &&
                        existing.dateDay == rec.dateDay &&
                        existing.dateMonth == rec.dateMonth
            }

            if (matchingExisting != null && strategy == DuplicateStrategy.SKIP_DUPLICATES) {
                continue
            }

            val avatarUri = rec.avatarBase64?.let { base64ToFile(context, it) }
            val photoUris = rec.photosBase64.mapNotNull { base64ToFile(context, it) }

            val parsedEventType = try {
                EventType.valueOf(rec.eventType)
            } catch (_: Exception) {
                EventType.BIRTHDAY
            }

            val parsedRelationship = try {
                RelationshipType.valueOf(rec.relationship)
            } catch (_: Exception) {
                RelationshipType.FRIEND
            }

            if (matchingExisting != null && strategy == DuplicateStrategy.OVERWRITE_EXISTING) {
                val updatedEvent = matchingExisting.copy(
                    name = rec.name,
                    dateDay = rec.dateDay,
                    dateMonth = rec.dateMonth,
                    birthYear = rec.birthYear,
                    eventType = parsedEventType,
                    relationship = parsedRelationship,
                    notes = if (rec.notes.isNotBlank()) rec.notes else matchingExisting.notes,
                    avatarUri = avatarUri ?: matchingExisting.avatarUri,
                    photoUris = if (photoUris.isNotEmpty()) photoUris else matchingExisting.photoUris,
                    reminderDaysBefore = rec.reminderDaysBefore,
                    reminderHour = rec.reminderHour,
                    reminderMinute = rec.reminderMinute,
                    isReminderEnabled = rec.isReminderEnabled
                )
                onUpdateEvent(updatedEvent)
                importedCount++
            } else {
                // Insert new
                val newEvent = EventEntity(
                    name = rec.name,
                    dateDay = rec.dateDay,
                    dateMonth = rec.dateMonth,
                    birthYear = rec.birthYear,
                    eventType = parsedEventType,
                    relationship = parsedRelationship,
                    notes = rec.notes,
                    avatarUri = avatarUri,
                    photoUris = photoUris,
                    reminderDaysBefore = rec.reminderDaysBefore,
                    reminderHour = rec.reminderHour,
                    reminderMinute = rec.reminderMinute,
                    isReminderEnabled = rec.isReminderEnabled
                )
                val newId = onInsertEvent(newEvent)

                // Add imported memories if any
                for (mem in rec.memories) {
                    val memPhoto = mem.photoBase64?.let { base64ToFile(context, it) }
                    onInsertMemory(
                        MemoryEntity(
                            eventId = newId,
                            year = mem.year,
                            title = mem.title,
                            description = mem.description,
                            photoUri = memPhoto
                        )
                    )
                }
                importedCount++
            }
        }
        importedCount
    }
}
