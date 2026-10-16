package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpecialDaysData
import com.example.model.EventEntity
import com.example.model.EventType
import com.example.model.Language
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme
import com.example.util.DateUtils
import java.time.LocalDate

@Composable
fun SpecialDaysScreen(
    customEvents: List<EventEntity>,
    language: Language,
    onAddCustomSpecialDay: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = DiesTheme.colors
    var selectedCategory by remember { mutableStateOf("All") }
    val today = remember { LocalDate.now() }

    val categoriesEn = listOf("All", "National", "Festival", "International", "Custom")
    val categoriesHi = listOf("सभी", "राष्ट्रीय दिवस", "त्योहार", "अंतरराष्ट्रीय", "व्यक्तिगत")

    // Custom special day events added by the user
    val customSpecialEvents = remember(customEvents) {
        customEvents.filter { it.eventType == EventType.SPECIAL_DAY || it.eventType == EventType.OTHER }
    }

    val filteredBuiltIn = remember(selectedCategory) {
        if (selectedCategory == "All") {
            SpecialDaysData.items
        } else if (selectedCategory == "Custom") {
            emptyList()
        } else {
            SpecialDaysData.items.filter { it.categoryEn == selectedCategory }
        }
    }

    // Sort by upcoming date
    val sortedBuiltIn = remember(filteredBuiltIn) {
        filteredBuiltIn.sortedBy { DateUtils.getDaysUntilNext(it.day, it.month, today) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Add Custom Special Day Bar
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.HINDI) "विशेष दिवस एवं त्योहार" else "SPECIAL DAYS & FESTIVALS",
                            color = colors.goldenYellow,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (language == Language.HINDI) "अपना विशेष दिवस या पर्व भी जोड़ सकते हैं" else "Track national days, holidays & your own events",
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onAddCustomSpecialDay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.goldenYellow,
                            contentColor = androidx.compose.ui.graphics.Color(0xFF242A2E)
                        ),
                        shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                        modifier = Modifier.testTag("add_custom_special_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == Language.HINDI) "जोड़ें" else "ADD",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoriesEn.forEachIndexed { index, catKey ->
                    val isSelected = selectedCategory == catKey
                    val label = if (language == Language.HINDI) categoriesHi[index] else catKey
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                            .background(if (isSelected) colors.goldenYellow else colors.surface)
                            .border(
                                1.dp,
                                if (isSelected) colors.goldenYellow else colors.surfaceBorder,
                                CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                            )
                            .clickable { selectedCategory = catKey }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) androidx.compose.ui.graphics.Color(0xFF242A2E) else colors.textPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // User's custom special events section (if All or Custom is selected)
        if (selectedCategory in listOf("All", "Custom") && customSpecialEvents.isNotEmpty()) {
            item {
                Text(
                    text = if (language == Language.HINDI) "आपके जोड़े गए विशेष अवसर" else "YOUR CUSTOM OCCASIONS",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(customSpecialEvents, key = { "custom_sp_${it.id}" }) { event ->
                val countdown = DateUtils.getCountdownLabel(event.dateDay, event.dateMonth, language)
                val formattedDate = DateUtils.formatDate(event.dateDay, event.dateMonth, event.birthYear, language)

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = colors.sageTeal.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.name,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedDate,
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                            if (event.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = event.notes,
                                    color = colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        NeonBadge(text = countdown, accentColor = colors.sageTeal)
                    }
                }
            }
        }

        // Built-in Holidays & Festivals List
        if (selectedCategory != "Custom") {
            item {
                Text(
                    text = if (language == Language.HINDI) "प्रमुख दिवस एवं त्योहार" else "OFFICIAL DAYS & CELEBRATIONS",
                    color = colors.textSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(sortedBuiltIn, key = { it.id }) { item ->
                val countdown = DateUtils.getCountdownLabel(item.day, item.month, language)
                val daysUntil = DateUtils.getDaysUntilNext(item.day, item.month, today)
                val formattedDate = "${item.day} ${DateUtils.getMonthShort(item.month, language)}"

                val badgeColor = when {
                    daysUntil == 0L -> colors.goldenYellow
                    daysUntil <= 3L -> colors.sageTeal
                    daysUntil <= 14L -> colors.warmCoral
                    else -> colors.textMuted
                }

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (daysUntil <= 3L) colors.goldenYellow.copy(alpha = 0.6f) else colors.surfaceBorder
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.goldenYellow.copy(alpha = 0.2f))
                                        .border(1.dp, colors.goldenYellow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = colors.goldenYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == Language.HINDI) item.nameHi else item.nameEn,
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "$formattedDate • ${if (language == Language.HINDI) item.categoryHi else item.categoryEn}",
                                        color = colors.textSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            NeonBadge(text = countdown, accentColor = badgeColor)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == Language.HINDI) item.descriptionHi else item.descriptionEn,
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Share / Copy greeting for the festival
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val greetingText = if (language == Language.HINDI) {
                                "${item.nameHi} की हार्दिक शुभकामनाएँ! 🪔✨ ईश्वर आपके जीवन में खुशियाँ और समृद्धि लाए।"
                            } else {
                                "Wishing you a very Happy ${item.nameEn}! ✨ May this special day bring joy, harmony, and prosperity to you and your loved ones."
                            }

                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(greetingText)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Greeting",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { shareText(context, greetingText) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Share Greeting",
                                    tint = colors.goldenYellow,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
