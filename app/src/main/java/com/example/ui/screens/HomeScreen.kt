package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WishesData
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.Language
import com.example.ui.components.CelebrationModal
import com.example.ui.components.ContactAvatar
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme
import com.example.util.DateUtils

@Composable
fun HomeScreen(
    events: List<EventEntity>,
    todayEvents: List<EventEntity>,
    searchQuery: String,
    language: Language,
    celebrationName: String?,
    onSearchChange: (String) -> Unit,
    onEventClick: (EventEntity) -> Unit,
    onAddClick: () -> Unit,
    onToggleWished: (EventEntity) -> Unit,
    onDismissCelebration: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = DiesTheme.colors
    var copiedMessageNotice by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Search Input with Palette Colors
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = if (language == Language.HINDI) "खोजें... (नाम या नोट्स)" else "> SEARCH BY NAME OR NOTES...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = colors.textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.sageTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.sageTeal,
                        unfocusedBorderColor = colors.surfaceBorder,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    singleLine = true
                )
            }

            // Celebration Modal / Banner
            if (celebrationName != null) {
                item {
                    CelebrationModal(
                        personName = celebrationName,
                        language = language,
                        onDismiss = onDismissCelebration
                    )
                }
            }

            // SECTION 1: TODAY'S WISHES (आज किसे विश करना है?)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (todayEvents.isNotEmpty()) colors.goldenYellow else colors.sageTeal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.HINDI) "आज किसे विश करना है? [TODAY]" else "WHO TO WISH TODAY? [TODAY]",
                        color = if (todayEvents.isNotEmpty()) colors.goldenYellow else colors.textSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (todayEvents.isNotEmpty()) {
                items(todayEvents, key = { "today_${it.id}" }) { event ->
                    TodayEventCard(
                        event = event,
                        language = language,
                        onCardClick = { onEventClick(event) },
                        onWishClick = {
                            val defaultWish = WishesData.messages.firstOrNull { m ->
                                if (event.eventType == EventType.ANNIVERSARY) m.categoryEn == "Anniversary" else m.categoryEn == "Birthday"
                            }
                            val rawText = if (language == Language.HINDI) defaultWish?.textHi else defaultWish?.textEn
                            val textToSend = (rawText ?: "Happy Birthday!").replace("{name}", event.name)
                            shareText(context, textToSend)
                        },
                        onCopyClick = {
                            val defaultWish = WishesData.messages.firstOrNull { m ->
                                if (event.eventType == EventType.ANNIVERSARY) m.categoryEn == "Anniversary" else m.categoryEn == "Birthday"
                            }
                            val rawText = if (language == Language.HINDI) defaultWish?.textHi else defaultWish?.textEn
                            val textToCopy = (rawText ?: "Happy Birthday!").replace("{name}", event.name)
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            copiedMessageNotice = event.name
                        },
                        onToggleWished = { onToggleWished(event) }
                    )
                }
            } else {
                item {
                    CyberCard(
                        borderColor = colors.surfaceBorder.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "आज कोई जन्मदिन या सालगिरह नहीं है।" else "NO BIRTHDAYS OR OCCASIONS TODAY.",
                                color = colors.textMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.HINDI) "आने वाले दिनों के लिए नीचे देखें।" else "Check upcoming occasions below.",
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // SECTION 2: ALL / UPCOMING COUNTDOWNS
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.sageTeal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.HINDI) "आने वाले जन्मदिन व अवसर" else "UPCOMING CELEBRATIONS",
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "[TOTAL: ${events.size}]",
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            if (events.isEmpty()) {
                item {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = colors.goldenYellow,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (language == Language.HINDI) "कोई जन्मदिन नहीं मिला" else "NO OCCASIONS FOUND",
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.HINDI) "नया जन्मदिन जोड़ने के लिए नीचे दिए गए + बटन को दबाएँ।" else "Tap the + button below to add your first birthday or anniversary.",
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(events, key = { it.id }) { event ->
                    EventRowCard(
                        event = event,
                        language = language,
                        onClick = { onEventClick(event) },
                        onToggleWished = { onToggleWished(event) }
                    )
                }
            }

            // Bottom Spacing for FAB
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_event_fab"),
            containerColor = colors.goldenYellow,
            contentColor = Color(0xFF242A2E),
            shape = CutCornerShape(topEnd = 14.dp, bottomStart = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Birthday",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == Language.HINDI) "नया जोड़ें" else "ADD EVENT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun TodayEventCard(
    event: EventEntity,
    language: Language,
    onCardClick: () -> Unit,
    onWishClick: () -> Unit,
    onCopyClick: () -> Unit,
    onToggleWished: () -> Unit
) {
    val colors = DiesTheme.colors
    val shape = CutCornerShape(topEnd = 14.dp, bottomStart = 14.dp)
    val ageInfo = DateUtils.getAgeInfo(event)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.5.dp, colors.goldenYellow, shape)
            .background(colors.todayHighlightBg)
            .clickable { onCardClick() }
            .padding(14.dp)
            .testTag("today_event_card_${event.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ContactAvatar(name = event.name, avatarUri = event.avatarUri, sizeDp = 50)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = event.name,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
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
                    }
                }

                // Today badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.goldenYellow)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (language == Language.HINDI) "आज है! 🎂" else "TODAY! 🎂",
                        color = Color(0xFF242A2E),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            if (ageInfo != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == Language.HINDI)
                            "उम्र: ${ageInfo.turningAge} वर्ष (जन्म: ${event.birthYear})"
                        else
                            "TURNING ${ageInfo.turningAge} YEARS (BORN: ${event.birthYear})",
                        color = if (colors.isDark) colors.goldenYellow else Color(0xFF9E650D),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (event.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.notes,
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mark as wished toggle button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (event.isWishedThisYear) colors.sageTeal.copy(alpha = 0.22f) else colors.surfaceBorder)
                        .clickable { onToggleWished() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_wished_button_${event.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (event.isWishedThisYear) Icons.Default.Check else Icons.Outlined.CheckCircleOutline,
                        contentDescription = "Mark Wished",
                        tint = if (event.isWishedThisYear) colors.sageTeal else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.isWishedThisYear)
                            (if (language == Language.HINDI) "विश कर दिया ✓" else "WISHED ✓")
                        else
                            (if (language == Language.HINDI) "विश करें" else "MARK WISHED"),
                        color = if (event.isWishedThisYear) colors.sageTeal else colors.textPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Copy & Share buttons
                Row {
                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp))
                            .testTag("copy_wish_${event.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Wish",
                            tint = colors.sageTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                            .background(colors.sageTeal)
                            .clickable { onWishClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("share_wish_${event.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.HINDI) "शेयर करें" else "SHARE",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventRowCard(
    event: EventEntity,
    language: Language,
    onClick: () -> Unit,
    onToggleWished: () -> Unit
) {
    val colors = DiesTheme.colors
    val countdown = DateUtils.getCountdownLabel(event.dateDay, event.dateMonth, language)
    val daysUntil = DateUtils.getDaysUntilNext(event.dateDay, event.dateMonth)
    val ageInfo = DateUtils.getAgeInfo(event)
    val formattedDate = DateUtils.formatDate(event.dateDay, event.dateMonth, event.birthYear, language)

    val badgeColor = when {
        daysUntil == 0L -> colors.goldenYellow
        daysUntil <= 3L -> colors.sageTeal
        daysUntil <= 14L -> colors.warmCoral
        else -> colors.textMuted
    }

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        borderColor = if (daysUntil <= 3L) colors.sageTeal.copy(alpha = 0.6f) else colors.surfaceBorder,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                ContactAvatar(name = event.name, avatarUri = event.avatarUri, sizeDp = 44)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = event.name,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedDate,
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        if (ageInfo != null) {
                            Text(
                                text = " • ${if (language == Language.HINDI) "${ageInfo.turningAge} वर्ष" else "Age ${ageInfo.turningAge}"}",
                                color = colors.goldenYellow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                NeonBadge(text = countdown, accentColor = badgeColor)
                if (event.isWishedThisYear) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.HINDI) "विश किया ✓" else "WISHED ✓",
                        color = colors.sageTeal,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun shareText(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, "Share Wish via")
    context.startActivity(chooser)
}
