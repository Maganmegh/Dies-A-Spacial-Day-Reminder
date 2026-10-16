package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.Language
import com.example.model.RelationshipType
import com.example.ui.components.ContactAvatar
import com.example.ui.theme.DiesTheme
import com.example.util.DateUtils
import com.example.util.ExportImportManager
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AddEditEventDialog(
    initialEvent: EventEntity?,
    language: Language,
    onDismiss: () -> Unit,
    onSave: (EventEntity) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = DiesTheme.colors
    val today = remember { LocalDate.now() }

    var name by remember { mutableStateOf(initialEvent?.name ?: "") }
    var selectedDay by remember { mutableIntStateOf(initialEvent?.dateDay ?: today.dayOfMonth) }
    var selectedMonth by remember { mutableIntStateOf(initialEvent?.dateMonth ?: today.monthValue) }

    var hasBirthYear by remember { mutableStateOf(initialEvent?.birthYear != null) }
    var birthYearText by remember { mutableStateOf(initialEvent?.birthYear?.toString() ?: "${today.year - 20}") }

    var selectedEventType by remember { mutableStateOf(initialEvent?.eventType ?: EventType.BIRTHDAY) }
    var selectedRelationship by remember { mutableStateOf(initialEvent?.relationship ?: RelationshipType.FRIEND) }
    var notes by remember { mutableStateOf(initialEvent?.notes ?: "") }

    var avatarUri by remember { mutableStateOf(initialEvent?.avatarUri) }
    var photoUris by remember { mutableStateOf(initialEvent?.photoUris ?: emptyList()) }

    var isReminderEnabled by remember { mutableStateOf(initialEvent?.isReminderEnabled ?: true) }
    var reminderDaysBefore by remember { mutableIntStateOf(initialEvent?.reminderDaysBefore ?: 0) }
    var reminderHour by remember { mutableIntStateOf(initialEvent?.reminderHour ?: 9) }
    var reminderMinute by remember { mutableIntStateOf(initialEvent?.reminderMinute ?: 0) }

    var isDayDropdownOpen by remember { mutableStateOf(false) }
    var isMonthDropdownOpen by remember { mutableStateOf(false) }
    var isHourDropdownOpen by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val saved = ExportImportManager.saveImageToInternalStorage(context, uri)
                if (saved != null) avatarUri = saved
            }
        }
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val savedList = mutableListOf<String>()
                for (u in uris) {
                    val saved = ExportImportManager.saveImageToInternalStorage(context, u)
                    if (saved != null) savedList.add(saved)
                }
                photoUris = photoUris + savedList
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialEvent == null) {
                    if (language == Language.HINDI) "नया जन्मदिन / अवसर जोड़ें" else "ADD NEW OCCASION"
                } else {
                    if (language == Language.HINDI) "अवसर संपादित करें" else "EDIT OCCASION"
                },
                color = colors.primaryAccent,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("event_name_input"),
                    label = {
                        Text(
                            text = if (language == Language.HINDI) "व्यक्ति या अवसर का नाम *" else "Person or Occasion Name *",
                            color = if (nameError) Color(0xFFFF5252) else colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    },
                    isError = nameError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = colors.sageTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.sageTeal,
                        unfocusedBorderColor = colors.surfaceBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    singleLine = true
                )

                // Event Type Chips
                Text(
                    text = if (language == Language.HINDI) "अवसर का प्रकार:" else "EVENT TYPE:",
                    color = colors.textSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventType.values().forEach { type ->
                        val isSelected = selectedEventType == type
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                                .background(if (isSelected) colors.goldenYellow else colors.surfaceVariant)
                                .border(
                                    0.8.dp,
                                    if (isSelected) colors.goldenYellow else colors.surfaceBorder,
                                    CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                                )
                                .clickable { selectedEventType = type }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (language == Language.HINDI) type.titleHi else type.titleEn,
                                color = if (isSelected) Color(0xFF242A2E) else colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Relationship Chips
                Text(
                    text = if (language == Language.HINDI) "संबंध:" else "RELATIONSHIP:",
                    color = colors.textSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RelationshipType.values().forEach { rel ->
                        val isSelected = selectedRelationship == rel
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                                .background(if (isSelected) colors.sageTeal else colors.surfaceVariant)
                                .border(
                                    0.8.dp,
                                    if (isSelected) colors.sageTeal else colors.surfaceBorder,
                                    CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                                )
                                .clickable { selectedRelationship = rel }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (language == Language.HINDI) rel.titleHi else rel.titleEn,
                                color = if (isSelected) Color.White else colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Date Selector (Day & Month)
                Text(
                    text = if (language == Language.HINDI) "तारीख (दिन व महीना):" else "DATE (DAY & MONTH):",
                    color = colors.textSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Day Picker
                    Box(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { isDayDropdownOpen = true }
                                .padding(12.dp)
                                .testTag("day_picker_dropdown")
                        ) {
                            Text(
                                text = "${selectedDay} ${if (language == Language.HINDI) "तारीख" else "Day"}",
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                        DropdownMenu(
                            expanded = isDayDropdownOpen,
                            onDismissRequest = { isDayDropdownOpen = false },
                            modifier = Modifier.background(colors.surface)
                        ) {
                            (1..31).forEach { day ->
                                DropdownMenuItem(
                                    text = { Text("$day", color = colors.textPrimary, fontFamily = FontFamily.Monospace) },
                                    onClick = {
                                        selectedDay = day
                                        isDayDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Month Picker
                    Box(modifier = Modifier.weight(1.5f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { isMonthDropdownOpen = true }
                                .padding(12.dp)
                                .testTag("month_picker_dropdown")
                        ) {
                            Text(
                                text = DateUtils.getMonthName(selectedMonth, language),
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(
                            expanded = isMonthDropdownOpen,
                            onDismissRequest = { isMonthDropdownOpen = false },
                            modifier = Modifier.background(colors.surface)
                        ) {
                            (1..12).forEach { m ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            DateUtils.getMonthName(m, language),
                                            color = colors.textPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    onClick = {
                                        selectedMonth = m
                                        isMonthDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Birth Year (Optional)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hasBirthYear,
                        onCheckedChange = { hasBirthYear = it },
                        colors = CheckboxDefaults.colors(checkedColor = colors.sageTeal)
                    )
                    Text(
                        text = if (language == Language.HINDI) "जन्म वर्ष पता है (उम्र गणना हेतु)" else "Include Birth Year (for exact age)",
                        color = colors.textPrimary,
                        fontSize = 12.sp
                    )
                }

                if (hasBirthYear) {
                    OutlinedTextField(
                        value = birthYearText,
                        onValueChange = { if (it.length <= 4) birthYearText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("birth_year_input"),
                        label = {
                            Text(
                                text = if (language == Language.HINDI) "जन्म वर्ष (उदा. 1998)" else "Birth Year (e.g. 1998)",
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.sageTeal,
                            unfocusedBorderColor = colors.surfaceBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        singleLine = true
                    )
                }

                // Avatar / Profile Picture Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ContactAvatar(name = name, avatarUri = avatarUri, sizeDp = 44)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == Language.HINDI) "प्रोफाइल फोटो" else "Profile Photo",
                                color = colors.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == Language.HINDI) "गैलरी से तस्वीर चुनें" else "Choose photo from gallery",
                                color = colors.textMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            avatarPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = colors.primaryAccent
                        ),
                        shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Pick Photo",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == Language.HINDI) "बदलें" else "SELECT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }

                // Additional Photos / Memories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${if (language == Language.HINDI) "संबंधित तस्वीरें" else "PHOTOS & MEMORIES"} (${photoUris.size})",
                        color = colors.textSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )

                    TextButton(onClick = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photos",
                            tint = colors.goldenYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (language == Language.HINDI) "फोटो जोड़ें" else "+ ADD MORE",
                            color = colors.goldenYellow,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (photoUris.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(photoUris) { pUri ->
                            Box(modifier = Modifier.size(54.dp)) {
                                AsyncImage(
                                    model = pUri,
                                    contentDescription = "Photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { photoUris = photoUris.filter { it != pUri } },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Reminder Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder",
                            tint = if (isReminderEnabled) colors.sageTeal else colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.HINDI) "स्मार्ट रिमाइंडर सक्षम करें" else "Enable Smart Reminder",
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Switch(
                        checked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.sageTeal,
                            checkedTrackColor = colors.sageTeal.copy(alpha = 0.4f)
                        )
                    )
                }

                if (isReminderEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (reminderDaysBefore == 0) colors.sageTeal.copy(alpha = 0.2f) else colors.surfaceVariant)
                                .border(
                                    0.8.dp,
                                    if (reminderDaysBefore == 0) colors.sageTeal else colors.surfaceBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { reminderDaysBefore = 0 }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "उसी दिन सुबह" else "On the Day",
                                color = if (reminderDaysBefore == 0) colors.sageTeal else colors.textSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (reminderDaysBefore == 1) colors.sageTeal.copy(alpha = 0.2f) else colors.surfaceVariant)
                                .border(
                                    0.8.dp,
                                    if (reminderDaysBefore == 1) colors.sageTeal else colors.surfaceBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { reminderDaysBefore = 1 }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "1 दिन पहले" else "1 Day Before",
                                color = if (reminderDaysBefore == 1) colors.sageTeal else colors.textSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Time Picker
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { isHourDropdownOpen = true }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${if (language == Language.HINDI) "समय:" else "Time:"} %02d:%02d ${if (reminderHour < 12) "AM" else "PM"}".format(
                                    if (reminderHour == 0 || reminderHour == 12) 12 else reminderHour % 12,
                                    reminderMinute
                                ),
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(
                            expanded = isHourDropdownOpen,
                            onDismissRequest = { isHourDropdownOpen = false },
                            modifier = Modifier.background(colors.surface)
                        ) {
                            listOf(7, 8, 9, 10, 11, 12, 14, 18, 20, 21).forEach { hr ->
                                val ampm = if (hr < 12) "AM" else "PM"
                                val displayHr = if (hr == 0 || hr == 12) 12 else hr % 12
                                DropdownMenuItem(
                                    text = { Text("%02d:00 $ampm".format(displayHr), color = colors.textPrimary, fontFamily = FontFamily.Monospace) },
                                    onClick = {
                                        reminderHour = hr
                                        reminderMinute = 0
                                        isHourDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Optional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = if (language == Language.HINDI) "व्यक्तिगत नोट्स या पसंद (वैकल्पिक)" else "Personal Notes or Gift Ideas (optional)",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    },
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.sageTeal,
                        unfocusedBorderColor = colors.surfaceBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val parsedYear = if (hasBirthYear) birthYearText.toIntOrNull() else null
                    val eventToSave = (initialEvent ?: EventEntity(
                        name = name.trim(),
                        dateDay = selectedDay,
                        dateMonth = selectedMonth
                    )).copy(
                        name = name.trim(),
                        dateDay = selectedDay,
                        dateMonth = selectedMonth,
                        birthYear = parsedYear,
                        eventType = selectedEventType,
                        relationship = selectedRelationship,
                        notes = notes.trim(),
                        avatarUri = avatarUri,
                        photoUris = photoUris,
                        reminderDaysBefore = reminderDaysBefore,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute,
                        isReminderEnabled = isReminderEnabled
                    )
                    onSave(eventToSave)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.sageTeal,
                    contentColor = Color.White
                ),
                shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                modifier = Modifier.testTag("save_event_button")
            ) {
                Text(
                    text = if (language == Language.HINDI) "सुरक्षित करें" else "SAVE EVENT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (language == Language.HINDI) "रद्द करें" else "CANCEL",
                    color = colors.textSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        containerColor = colors.surface,
        shape = CutCornerShape(topEnd = 14.dp, bottomStart = 14.dp)
    )
}
