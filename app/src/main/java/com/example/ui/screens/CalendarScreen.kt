package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpecialDaysData
import com.example.model.EventEntity
import com.example.model.Language
import com.example.ui.components.ContactAvatar
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme
import com.example.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    events: List<EventEntity>,
    language: Language,
    onEventClick: (EventEntity) -> Unit
) {
    val colors = DiesTheme.colors
    val today = remember { LocalDate.now() }
    var currentYearMonth by remember { mutableStateOf(YearMonth.of(today.year, today.monthValue)) }
    var selectedDay by remember { mutableStateOf(today.dayOfMonth) }

    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value // 1 (Mon) to 7 (Sun)

    // Events and special days in this month
    val monthEvents = remember(events, currentYearMonth) {
        events.filter { it.dateMonth == currentYearMonth.monthValue }
    }
    val monthSpecialDays = remember(currentYearMonth) {
        SpecialDaysData.items.filter { it.month == currentYearMonth.monthValue }
    }

    // Filtered by selected day
    val selectedDayEvents = monthEvents.filter { it.dateDay == selectedDay }
    val selectedDaySpecial = monthSpecialDays.filter { it.day == selectedDay }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Header Navigator
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.surfaceBorder, CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    modifier = Modifier.testTag("calendar_prev_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = colors.sageTeal
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${DateUtils.getMonthName(currentYearMonth.monthValue, language).uppercase()} ${currentYearMonth.year}",
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (language == Language.HINDI)
                            "${monthEvents.size} जन्मदिन • ${monthSpecialDays.size} विशेष दिन"
                        else
                            "${monthEvents.size} BIRTHDAYS • ${monthSpecialDays.size} SPECIAL",
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }

                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    modifier = Modifier.testTag("calendar_next_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = colors.sageTeal
                    )
                }
            }
        }

        // Days of week header (Mon - Sun)
        item {
            val weekDayHeaders = if (language == Language.HINDI)
                listOf("सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि", "रवि")
            else
                listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDayHeaders.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Calendar Grid Matrix
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.surfaceBorder, CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (row in 0 until 6) {
                    val firstCell = row * 7 + 1 - (firstDayOfWeek - 1)
                    if (firstCell > daysInMonth) break

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (col in 1..7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - (firstDayOfWeek - 1)

                            if (dayNumber in 1..daysInMonth) {
                                val isSelected = dayNumber == selectedDay
                                val isTodayDate = currentYearMonth.year == today.year &&
                                        currentYearMonth.monthValue == today.monthValue &&
                                        dayNumber == today.dayOfMonth

                                val hasBirthday = monthEvents.any { it.dateDay == dayNumber }
                                val hasSpecial = monthSpecialDays.any { it.day == dayNumber }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when {
                                                isSelected -> colors.sageTeal
                                                isTodayDate -> colors.goldenYellow.copy(alpha = 0.28f)
                                                else -> colors.surfaceVariant.copy(alpha = 0.45f)
                                            }
                                        )
                                        .border(
                                            width = if (isTodayDate && !isSelected) 1.dp else 0.dp,
                                            color = colors.goldenYellow,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { selectedDay = dayNumber }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            color = when {
                                                isSelected -> Color.White
                                                isTodayDate -> if (colors.isDark) colors.goldenYellow else Color(0xFF9E650D)
                                                else -> colors.textPrimary
                                            },
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected || isTodayDate) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )

                                        // Indicator Dots
                                        if (hasBirthday || hasSpecial) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 1.dp)
                                            ) {
                                                if (hasBirthday) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) Color.White else colors.sageTeal)
                                                    )
                                                }
                                                if (hasBirthday && hasSpecial) {
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                }
                                                if (hasSpecial) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) colors.goldenYellow else colors.goldenYellow)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Empty cell
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(colors.sageTeal))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == Language.HINDI) "जन्मदिन" else "Birthday",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(colors.goldenYellow))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == Language.HINDI) "त्योहार/दिवस" else "Special/Festival",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "${selectedDay} ${DateUtils.getMonthShort(currentYearMonth.monthValue, language)}",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Selected Day Details Header
        item {
            Text(
                text = if (language == Language.HINDI)
                    "चयनित तारीख के इवेंट्स (${selectedDay} ${DateUtils.getMonthName(currentYearMonth.monthValue, language)})"
                else
                    "EVENTS ON ${selectedDay} ${DateUtils.getMonthName(currentYearMonth.monthValue, language).uppercase()}",
                color = colors.textSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        if (selectedDayEvents.isEmpty() && selectedDaySpecial.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == Language.HINDI)
                            "इस तारीख पर कोई जन्मदिन या विशेष पर्व दर्ज नहीं है।"
                        else
                            "No events or holidays recorded for this day.",
                        color = colors.textMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
        }

        // Show birthdays on selected day
        items(selectedDayEvents, key = { "day_ev_${it.id}" }) { event ->
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = colors.sageTeal.copy(alpha = 0.6f),
                onClick = { onEventClick(event) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ContactAvatar(name = event.name, avatarUri = event.avatarUri, sizeDp = 42)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.name,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeonBadge(
                                text = if (language == Language.HINDI) event.eventType.titleHi else event.eventType.titleEn.uppercase(),
                                accentColor = colors.sageTeal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            DateUtils.getAgeInfo(event)?.let { age ->
                                Text(
                                    text = if (language == Language.HINDI) "${age.turningAge} वर्ष" else "Age ${age.turningAge}",
                                    color = colors.goldenYellow,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "[VIEW]",
                        color = colors.sageTeal,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Show special days / festivals on selected day
        items(selectedDaySpecial, key = { "day_sp_${it.id}" }) { item ->
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = colors.goldenYellow.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.goldenYellow.copy(alpha = 0.2f))
                            .border(1.dp, colors.goldenYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.goldenYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.HINDI) item.nameHi else item.nameEn,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (language == Language.HINDI) item.descriptionHi else item.descriptionEn,
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
