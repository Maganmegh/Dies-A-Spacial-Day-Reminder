package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Language
import com.example.ui.theme.DiesTheme

@Composable
fun CyberHeader(
    currentLanguage: Language,
    isDarkMode: Boolean,
    onLanguageToggle: () -> Unit,
    onThemeToggle: () -> Unit
) {
    val colors = DiesTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Retro blinking status indicator using user's golden yellow / teal
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colors.goldenYellow)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DIES",
                        color = colors.primaryAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (currentLanguage == Language.HINDI) "स्मार्ट मेमोरी कैलेंडर" else "SMART MEMORY CALENDAR",
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Light / Dark Mode Toggle Button
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                        .border(
                            width = 1.dp,
                            color = colors.surfaceBorder,
                            shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                        )
                        .background(colors.surface)
                        .clickable { onThemeToggle() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("theme_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Light/Dark Theme",
                            tint = colors.goldenYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDarkMode) "LIGHT" else "DARK",
                            color = colors.textPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Language Switcher Badge Button
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                        .border(
                            width = 1.dp,
                            color = colors.surfaceBorder,
                            shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                        )
                        .background(colors.surface)
                        .clickable { onLanguageToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("language_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = colors.sageTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == Language.HINDI) "हिन्दी" else "EN",
                            color = colors.textPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = DiesTheme.colors
    val border = borderColor ?: colors.surfaceBorder
    val shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
    val cardModifier = modifier
        .clip(shape)
        .border(width = 1.dp, color = border, shape = shape)
        .background(
            backgroundColor ?: colors.surface
        )
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(14.dp)

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun NeonBadge(
    text: String,
    accentColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val colors = DiesTheme.colors
    val accent = accentColor ?: colors.primaryAccent
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(0.8.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .background(accent.copy(alpha = 0.16f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = if (colors.isDark) accent else Color(0xFF2E403C),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ContactAvatar(
    name: String,
    avatarUri: String?,
    modifier: Modifier = Modifier,
    sizeDp: Int = 46
) {
    val colors = DiesTheme.colors
    val shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
    if (!avatarUri.isNullOrBlank()) {
        AsyncImage(
            model = avatarUri,
            contentDescription = name,
            modifier = modifier
                .size(sizeDp.dp)
                .clip(shape)
                .border(1.dp, colors.sageTeal, shape),
            contentScale = ContentScale.Crop
        )
    } else {
        val initial = if (name.isNotBlank()) name.take(1).uppercase() else "?"
        Box(
            modifier = modifier
                .size(sizeDp.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(colors.surfaceVariant, colors.softBlush.copy(alpha = 0.35f))
                    )
                )
                .border(1.dp, colors.sageTeal.copy(alpha = 0.6f), shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = colors.textPrimary,
                fontSize = (sizeDp / 2.2).sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun RetroBottomNav(
    selectedTab: Int,
    currentLanguage: Language,
    onTabSelected: (Int) -> Unit
) {
    val colors = DiesTheme.colors
    val items = listOf(
        NavigationItemData(
            title = if (currentLanguage == Language.HINDI) "आज" else "Today",
            icon = Icons.Default.Celebration,
            tag = "tab_today"
        ),
        NavigationItemData(
            title = if (currentLanguage == Language.HINDI) "कैलेंडर" else "Calendar",
            icon = Icons.Default.CalendarMonth,
            tag = "tab_calendar"
        ),
        NavigationItemData(
            title = if (currentLanguage == Language.HINDI) "संदेश" else "Wishes",
            icon = Icons.Default.AutoAwesome,
            tag = "tab_wishes"
        ),
        NavigationItemData(
            title = if (currentLanguage == Language.HINDI) "विशेष दिन" else "Special",
            icon = Icons.Default.Star,
            tag = "tab_special"
        ),
        NavigationItemData(
            title = if (currentLanguage == Language.HINDI) "बैकअप" else "Data",
            icon = Icons.Default.Settings,
            tag = "tab_data"
        )
    )

    NavigationBar(
        containerColor = colors.background,
        contentColor = colors.textPrimary,
        tonalElevation = 6.dp
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedTab == index
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) colors.primaryAccent else colors.textMuted
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) colors.primaryAccent else colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colors.surfaceVariant
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}

private data class NavigationItemData(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun CelebrationModal(
    personName: String?,
    language: Language,
    onDismiss: () -> Unit
) {
    val colors = DiesTheme.colors
    AnimatedVisibility(
        visible = personName != null,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        if (personName != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                    .background(colors.celebrationBg)
                    .border(1.5.dp, colors.sageTeal, CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                    .clickable { onDismiss() }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = colors.sageTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == Language.HINDI) "शुभकामनाएँ भेज दी गईं! 🎉" else "WISHED! 🎉",
                                color = colors.sageTeal,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (language == Language.HINDI) "$personName को विश कर दिया गया।" else "Marked $personName as wished this year.",
                                color = colors.textPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Text(
                        text = "[DISMISS]",
                        color = colors.sageTeal,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
