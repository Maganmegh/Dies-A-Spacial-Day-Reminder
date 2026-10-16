package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.DiesApplication
import com.example.data.EventRepository
import com.example.model.EventEntity
import com.example.model.Language
import com.example.model.MemoryEntity
import com.example.reminder.ReminderScheduler
import com.example.util.DateUtils
import com.example.util.DuplicateStrategy
import com.example.util.ExportImportManager
import com.example.util.ImportPreviewResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    application: Application,
    private val repository: EventRepository
) : AndroidViewModel(application) {

    private val _currentLanguage = MutableStateFlow(Language.HINDI)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false) // Default to light mode with user's palette
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isTimelineEnabled = MutableStateFlow(true)
    val isTimelineEnabled: StateFlow<Boolean> = _isTimelineEnabled.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    val rawEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sorted by upcoming countdown
    val sortedEvents: StateFlow<List<EventEntity>> = combine(rawEvents, _searchQuery) { events, query ->
        val filtered = if (query.isBlank()) {
            events
        } else {
            events.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.notes.contains(query, ignoreCase = true)
            }
        }
        val today = LocalDate.now()
        filtered.sortedBy { DateUtils.getDaysUntilNext(it.dateDay, it.dateMonth, today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Events happening today
    val todayEvents: StateFlow<List<EventEntity>> = rawEvents.combine(_currentLanguage) { events, _ ->
        val today = LocalDate.now()
        events.filter { DateUtils.isToday(it.dateDay, it.dateMonth, today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & UI states
    private val _detailEvent = MutableStateFlow<EventEntity?>(null)
    val detailEvent: StateFlow<EventEntity?> = _detailEvent.asStateFlow()

    private val _editEvent = MutableStateFlow<EventEntity?>(null)
    val editEvent: StateFlow<EventEntity?> = _editEvent.asStateFlow()

    private val _isAddEditOpen = MutableStateFlow(false)
    val isAddEditOpen: StateFlow<Boolean> = _isAddEditOpen.asStateFlow()

    private val _celebrationEventName = MutableStateFlow<String?>(null)
    val celebrationEventName: StateFlow<String?> = _celebrationEventName.asStateFlow()

    private val _importPreview = MutableStateFlow<ImportPreviewResult?>(null)
    val importPreview: StateFlow<ImportPreviewResult?> = _importPreview.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
    }

    fun toggleTimeline(enabled: Boolean) {
        _isTimelineEnabled.value = enabled
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog() {
        _editEvent.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(event: EventEntity) {
        _editEvent.value = event
        _isAddEditOpen.value = true
    }

    fun closeAddEditDialog() {
        _isAddEditOpen.value = false
        _editEvent.value = null
    }

    fun openDetail(event: EventEntity) {
        _detailEvent.value = event
    }

    fun closeDetail() {
        _detailEvent.value = null
    }

    fun clearCelebration() {
        _celebrationEventName.value = null
    }

    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            val id = if (event.id == 0L) {
                repository.insertEvent(event)
            } else {
                repository.updateEvent(event)
                event.id
            }
            // Update or schedule reminder
            val savedEvent = event.copy(id = id)
            ReminderScheduler.scheduleReminder(getApplication(), savedEvent)

            // If detail dialog is currently showing this event, update it
            if (_detailEvent.value?.id == id) {
                _detailEvent.value = savedEvent
            }
            closeAddEditDialog()
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), eventId)
            repository.deleteEvent(eventId)
            if (_detailEvent.value?.id == eventId) {
                _detailEvent.value = null
            }
        }
    }

    fun toggleWishedStatus(event: EventEntity) {
        viewModelScope.launch {
            val currentYear = LocalDate.now().year
            val newStatus = !event.isWishedThisYear
            repository.setWishedStatus(event.id, newStatus, if (newStatus) currentYear else null)
            if (newStatus) {
                _celebrationEventName.value = event.name
            }
            if (_detailEvent.value?.id == event.id) {
                _detailEvent.value = _detailEvent.value?.copy(
                    isWishedThisYear = newStatus,
                    wishedYear = if (newStatus) currentYear else null
                )
            }
        }
    }

    // Memories
    fun getMemories(eventId: Long) = repository.getMemoriesForEvent(eventId)

    fun addMemory(eventId: Long, year: Int, title: String, description: String, photoUri: String?) {
        viewModelScope.launch {
            repository.insertMemory(
                MemoryEntity(
                    eventId = eventId,
                    year = year,
                    title = title,
                    description = description,
                    photoUri = photoUri
                )
            )
        }
    }

    fun deleteMemory(memoryId: Long) {
        viewModelScope.launch {
            repository.deleteMemory(memoryId)
        }
    }

    // Photos
    fun getPhotosForEvent(eventId: Long): Flow<List<com.example.model.Photo>> = repository.getPhotosForEvent(eventId)

    fun addPhoto(eventId: Long, uri: String, caption: String = "", isAvatar: Boolean = false) {
        viewModelScope.launch {
            repository.insertPhoto(com.example.model.Photo(eventId = eventId, uri = uri, caption = caption, isAvatar = isAvatar))
        }
    }

    fun deletePhoto(photoId: Long) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
        }
    }

    // Export & Import
    fun exportEvents(
        selectedIds: Set<Long>?,
        includePhotos: Boolean,
        onReady: (Uri) -> Unit
    ) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val all = repository.getAllEventsDirect()
                val targetList = if (selectedIds.isNullOrEmpty()) all else all.filter { it.id in selectedIds }
                val (_, contentUri) = ExportImportManager.exportEvents(
                    context = getApplication(),
                    events = targetList,
                    memoriesProvider = { eventId -> repository.getMemoriesForEventDirect(eventId) },
                    includePhotos = includePhotos
                )
                onReady(contentUri)
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun previewImport(uri: Uri) {
        viewModelScope.launch {
            _isImporting.value = true
            val existing = repository.getAllEventsDirect()
            val result = ExportImportManager.parseAndPreviewImport(getApplication(), uri, existing)
            _importPreview.value = result
            _isImporting.value = false
        }
    }

    fun clearImportPreview() {
        _importPreview.value = null
    }

    fun confirmImport(strategy: DuplicateStrategy, onComplete: (Int) -> Unit) {
        val payload = _importPreview.value?.payload ?: return
        viewModelScope.launch {
            _isImporting.value = true
            val existing = repository.getAllEventsDirect()
            val importedCount = ExportImportManager.executeImport(
                context = getApplication(),
                payload = payload,
                strategy = strategy,
                existingEvents = existing,
                onInsertEvent = { repository.insertEvent(it) },
                onUpdateEvent = { repository.updateEvent(it) },
                onInsertMemory = { repository.insertMemory(it) }
            )
            // Schedule reminders for new events
            val updatedAll = repository.getAllEventsDirect()
            for (ev in updatedAll) {
                if (ev.isReminderEnabled) {
                    ReminderScheduler.scheduleReminder(getApplication(), ev)
                }
            }
            _importPreview.value = null
            _isImporting.value = false
            onComplete(importedCount)
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: EventRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
