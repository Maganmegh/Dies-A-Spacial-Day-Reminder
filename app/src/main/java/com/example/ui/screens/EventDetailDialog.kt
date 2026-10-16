package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.WishesData
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.Language
import com.example.model.MemoryEntity
import com.example.ui.components.ContactAvatar
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme
import com.example.util.DateUtils
import com.example.util.ExportImportManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun EventDetailDialog(
    event: EventEntity,
    language: Language,
    isTimelineEnabled: Boolean,
    memoriesFlow: Flow<List<MemoryEntity>>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleWished: () -> Unit,
    onAddMemory: (year: Int, title: String, desc: String, photoUri: String?) -> Unit,
    onDeleteMemory: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = DiesTheme.colors
    val memories by memoriesFlow.collectAsState(initial = emptyList())

    val ageInfo = DateUtils.getAgeInfo(event)
    val countdown = DateUtils.getCountdownLabel(event.dateDay, event.dateMonth, language)
    val daysUntil = DateUtils.getDaysUntilNext(event.dateDay, event.dateMonth)
    val formattedDate = DateUtils.formatDate(event.dateDay, event.dateMonth, event.birthYear, language)
    val zodiac = DateUtils.getZodiacSign(event.dateDay, event.dateMonth, language)

    var isDeleteConfirmOpen by remember { mutableStateOf(false) }
    var isAddMemoryModalOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language == Language.HINDI) "व्यक्तिगत विवरण" else "PERSON PROFILE",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = colors.sageTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = { isDeleteConfirmOpen = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Header Card
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ContactAvatar(name = event.name, avatarUri = event.avatarUri, sizeDp = 60)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = event.name,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NeonBadge(
                                    text = if (language == Language.HINDI) event.eventType.titleHi else event.eventType.titleEn.uppercase(),
                                    accentColor = colors.goldenYellow
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                NeonBadge(
                                    text = if (language == Language.HINDI) event.relationship.titleHi else event.relationship.titleEn.uppercase(),
                                    accentColor = colors.sageTeal
                                )
                            }
                            if (zodiac.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "★ $zodiac",
                                    color = colors.goldenYellow,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Countdown & Age Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.surfaceBorder, CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedDate,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (ageInfo != null) {
                            Text(
                                text = if (language == Language.HINDI)
                                    "वर्तमान उम्र: ${ageInfo.currentAge} • अगली: ${ageInfo.turningAge} वर्ष"
                                else
                                    "Age: ${ageInfo.currentAge} • Next: ${ageInfo.turningAge} yrs",
                                color = colors.goldenYellow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = if (language == Language.HINDI) "जन्म वर्ष दर्ज नहीं है" else "Year not specified",
                                color = colors.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    NeonBadge(
                        text = countdown,
                        accentColor = if (daysUntil == 0L) colors.goldenYellow else colors.sageTeal
                    )
                }

                // Wished Button & Quick Wish Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mark as wished
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (event.isWishedThisYear) colors.sageTeal.copy(alpha = 0.22f) else colors.surfaceVariant)
                            .border(
                                1.dp,
                                if (event.isWishedThisYear) colors.sageTeal else colors.surfaceBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onToggleWished() }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (event.isWishedThisYear) Icons.Default.Check else Icons.Outlined.CheckCircleOutline,
                                contentDescription = null,
                                tint = if (event.isWishedThisYear) colors.sageTeal else colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (event.isWishedThisYear)
                                    (if (language == Language.HINDI) "विश किया ✓" else "WISHED ✓")
                                else
                                    (if (language == Language.HINDI) "विश कर दिया" else "MARK WISHED"),
                                color = if (event.isWishedThisYear) colors.sageTeal else colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Share wish now
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                            .background(colors.sageTeal)
                            .clickable {
                                val defaultWish = WishesData.messages.firstOrNull { m ->
                                    if (event.eventType == EventType.ANNIVERSARY) m.categoryEn == "Anniversary" else m.categoryEn == "Birthday"
                                }
                                val rawText = if (language == Language.HINDI) defaultWish?.textHi else defaultWish?.textEn
                                val textToSend = (rawText ?: "Happy Birthday!").replace("{name}", event.name)
                                shareText(context, textToSend)
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Wish",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.HINDI) "विश भेजें" else "WISH NOW",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Notes section if present
                if (event.notes.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (language == Language.HINDI) "नोट्स व उपहार विचार:" else "NOTES & PREFERENCES:",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = event.notes,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Photo Gallery section
                if (event.photoUris.isNotEmpty()) {
                    Column {
                        Text(
                            text = if (language == Language.HINDI) "तस्वीरें (${event.photoUris.size}):" else "SAVED PHOTOS (${event.photoUris.size}):",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(event.photoUris) { pUri ->
                                AsyncImage(
                                    model = pUri,
                                    contentDescription = "Photo",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, colors.surfaceBorder, RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Reminder status badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = if (event.isReminderEnabled) colors.sageTeal else colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.isReminderEnabled) {
                            if (event.reminderDaysBefore == 0) {
                                if (language == Language.HINDI) "रिमाइंडर: उसी दिन सुबह %02d:%02d बजे".format(event.reminderHour, event.reminderMinute)
                                else "Reminder: On the day at %02d:%02d".format(event.reminderHour, event.reminderMinute)
                            } else {
                                if (language == Language.HINDI) "रिमाइंडर: 1 दिन पहले %02d:%02d बजे".format(event.reminderHour, event.reminderMinute)
                                else "Reminder: 1 day before at %02d:%02d".format(event.reminderHour, event.reminderMinute)
                            }
                        } else {
                            if (language == Language.HINDI) "रिमाइंडर बंद है" else "Reminder disabled"
                        },
                        color = colors.textSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                // SECTION 10: OPTIONAL MEMORY TIMELINE
                if (isTimelineEnabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = colors.goldenYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.HINDI) "मेमोरी टाइमलाइन" else "MEMORY TIMELINE",
                                color = colors.goldenYellow,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        TextButton(onClick = { isAddMemoryModalOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = colors.sageTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (language == Language.HINDI) "+ नई याद" else "+ ADD MEMORY",
                                color = colors.sageTeal,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (memories.isEmpty()) {
                        Text(
                            text = if (language == Language.HINDI)
                                "अभी कोई पुरानी याद दर्ज नहीं है। पिछले वर्षों के उत्सव और यादें जोड़ने के लिए + बटन दबाएँ।"
                            else
                                "No memories logged yet. Tap + to record milestones and celebrations from previous years.",
                            color = colors.textMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            memories.forEach { mem ->
                                CyberCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    borderColor = colors.surfaceBorder
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                NeonBadge(text = "${mem.year}", accentColor = colors.goldenYellow)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = mem.title,
                                                    color = colors.textPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            if (mem.description.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = mem.description,
                                                    color = colors.textSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            if (!mem.photoUri.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                AsyncImage(
                                                    model = mem.photoUri,
                                                    contentDescription = "Memory photo",
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .clip(RoundedCornerShape(6.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteMemory(mem.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete memory",
                                                tint = colors.textMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant, contentColor = colors.textPrimary),
                shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
            ) {
                Text(
                    text = if (language == Language.HINDI) "बंद करें" else "CLOSE",
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        containerColor = colors.surface,
        shape = CutCornerShape(topEnd = 14.dp, bottomStart = 14.dp)
    )

    // Delete Confirmation Dialog
    if (isDeleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { isDeleteConfirmOpen = false },
            title = {
                Text(
                    text = if (language == Language.HINDI) "डिलीट की पुष्टि" else "CONFIRM DELETE",
                    color = Color(0xFFFF5252),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == Language.HINDI)
                        "क्या आप सचमुच ${event.name} का रिकॉर्ड हटाना चाहते हैं?"
                    else
                        "Are you sure you want to delete ${event.name}'s record?",
                    color = colors.textPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleteConfirmOpen = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                ) {
                    Text(
                        text = if (language == Language.HINDI) "हाँ, हटाएं" else "DELETE",
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteConfirmOpen = false }) {
                    Text(text = if (language == Language.HINDI) "रद्द करें" else "CANCEL", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
        )
    }

    // Add Memory Timeline Entry Modal
    if (isAddMemoryModalOpen) {
        var memYear by remember { mutableStateOf("${LocalDate.now().year - 1}") }
        var memTitle by remember { mutableStateOf("") }
        var memDesc by remember { mutableStateOf("") }
        var memPhotoUri by remember { mutableStateOf<String?>(null) }

        val memPhotoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    val saved = ExportImportManager.saveImageToInternalStorage(context, uri)
                    if (saved != null) memPhotoUri = saved
                }
            }
        }

        AlertDialog(
            onDismissRequest = { isAddMemoryModalOpen = false },
            title = {
                Text(
                    text = if (language == Language.HINDI) "नई याद जोड़ें" else "ADD MEMORY MILESTONE",
                    color = colors.goldenYellow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = memYear,
                        onValueChange = { if (it.length <= 4) memYear = it },
                        label = { Text("Year (e.g. 2024)", color = colors.textSecondary, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.goldenYellow,
                            unfocusedBorderColor = colors.surfaceBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = memTitle,
                        onValueChange = { memTitle = it },
                        label = { Text("Title (e.g. Birthday Party)", color = colors.textSecondary, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.goldenYellow,
                            unfocusedBorderColor = colors.surfaceBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = memDesc,
                        onValueChange = { memDesc = it },
                        label = { Text("Notes / Highlights", color = colors.textSecondary, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.goldenYellow,
                            unfocusedBorderColor = colors.surfaceBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        minLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Memory Photo", color = colors.textSecondary, fontSize = 12.sp)
                        Button(
                            onClick = {
                                memPhotoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant, contentColor = colors.primaryAccent),
                            shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                        ) {
                            Text(text = if (memPhotoUri != null) "Change Photo" else "Pick Photo", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val y = memYear.toIntOrNull() ?: LocalDate.now().year
                        if (memTitle.isNotBlank()) {
                            onAddMemory(y, memTitle, memDesc, memPhotoUri)
                            isAddMemoryModalOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.goldenYellow,
                        contentColor = Color(0xFF242A2E)
                    ),
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                ) {
                    Text(text = if (language == Language.HINDI) "सेव करें" else "SAVE", fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddMemoryModalOpen = false }) {
                    Text(text = if (language == Language.HINDI) "रद्द करें" else "CANCEL", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
        )
    }
}
