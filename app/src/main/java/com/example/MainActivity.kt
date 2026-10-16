package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.model.EventType
import com.example.model.Language
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.components.CyberHeader
import com.example.ui.components.RetroBottomNav
import com.example.ui.screens.AddEditEventDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.EventDetailDialog
import com.example.ui.screens.ExportImportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SpecialDaysScreen
import com.example.ui.screens.WishesScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as DiesApplication
        MainViewModelFactory(app, app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if opened from notification
        val openEventId = intent?.getLongExtra("OPEN_EVENT_ID", -1L) ?: -1L
        if (openEventId != -1L) {
            // Find and open detail when events load
        }

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                DiesApp(viewModel = viewModel, initialOpenEventId = openEventId, isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
fun DiesApp(
    viewModel: MainViewModel,
    initialOpenEventId: Long = -1L,
    isDarkMode: Boolean = false
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isTimelineEnabled by viewModel.isTimelineEnabled.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val events by viewModel.sortedEvents.collectAsState()
    val rawEvents by viewModel.rawEvents.collectAsState()
    val todayEvents by viewModel.todayEvents.collectAsState()

    val detailEvent by viewModel.detailEvent.collectAsState()
    val editEvent by viewModel.editEvent.collectAsState()
    val isAddEditOpen by viewModel.isAddEditOpen.collectAsState()
    val celebrationName by viewModel.celebrationEventName.collectAsState()
    val importPreview by viewModel.importPreview.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()

    val colors = com.example.ui.theme.DiesTheme.colors

    // Handle initial open from notification
    LaunchedEffect(rawEvents, initialOpenEventId) {
        if (initialOpenEventId != -1L && rawEvents.isNotEmpty()) {
            val target = rawEvents.find { it.id == initialOpenEventId }
            if (target != null) {
                viewModel.openDetail(target)
            }
        }
    }

    // Hardware BackHandler handling
    BackHandler(enabled = detailEvent != null || isAddEditOpen || selectedTab != 0) {
        when {
            detailEvent != null -> viewModel.closeDetail()
            isAddEditOpen -> viewModel.closeAddEditDialog()
            selectedTab != 0 -> viewModel.setTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CyberHeader(
                currentLanguage = language,
                isDarkMode = isDarkMode,
                onLanguageToggle = {
                    val nextLang = if (language == Language.HINDI) Language.ENGLISH else Language.HINDI
                    viewModel.setLanguage(nextLang)
                },
                onThemeToggle = {
                    viewModel.toggleDarkMode()
                }
            )
        },
        bottomBar = {
            RetroBottomNav(
                selectedTab = selectedTab,
                currentLanguage = language,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    events = events,
                    todayEvents = todayEvents,
                    searchQuery = searchQuery,
                    language = language,
                    celebrationName = celebrationName,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onEventClick = { viewModel.openDetail(it) },
                    onAddClick = { viewModel.openAddDialog() },
                    onToggleWished = { viewModel.toggleWishedStatus(it) },
                    onDismissCelebration = { viewModel.clearCelebration() }
                )
                1 -> CalendarScreen(
                    events = rawEvents,
                    language = language,
                    onEventClick = { viewModel.openDetail(it) }
                )
                2 -> WishesScreen(
                    contacts = rawEvents,
                    language = language
                )
                3 -> SpecialDaysScreen(
                    customEvents = rawEvents,
                    language = language,
                    onAddCustomSpecialDay = {
                        viewModel.openAddDialog()
                    }
                )
                4 -> ExportImportScreen(
                    events = rawEvents,
                    language = language,
                    isTimelineEnabled = isTimelineEnabled,
                    importPreview = importPreview,
                    isExporting = isExporting,
                    isImporting = isImporting,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onToggleTimeline = { viewModel.toggleTimeline(it) },
                    onExportRequested = { ids, photos, onReady ->
                        viewModel.exportEvents(ids, photos, onReady)
                    },
                    onFileSelectedForImport = { uri ->
                        viewModel.previewImport(uri)
                    },
                    onConfirmImport = { strategy, onDone ->
                        viewModel.confirmImport(strategy, onDone)
                    },
                    onClearImportPreview = {
                        viewModel.clearImportPreview()
                    }
                )
            }
        }
    }

    // Detail Dialog
    detailEvent?.let { ev ->
        EventDetailDialog(
            event = ev,
            language = language,
            isTimelineEnabled = isTimelineEnabled,
            memoriesFlow = viewModel.getMemories(ev.id),
            onDismiss = { viewModel.closeDetail() },
            onEdit = {
                viewModel.closeDetail()
                viewModel.openEditDialog(ev)
            },
            onDelete = {
                viewModel.deleteEvent(ev.id)
                viewModel.closeDetail()
            },
            onToggleWished = { viewModel.toggleWishedStatus(ev) },
            onAddMemory = { year, title, desc, photoUri ->
                viewModel.addMemory(ev.id, year, title, desc, photoUri)
            },
            onDeleteMemory = { memId ->
                viewModel.deleteMemory(memId)
            }
        )
    }

    // Add or Edit Event Dialog
    if (isAddEditOpen) {
        AddEditEventDialog(
            initialEvent = editEvent,
            language = language,
            onDismiss = { viewModel.closeAddEditDialog() },
            onSave = { saved -> viewModel.saveEvent(saved) }
        )
    }
}
