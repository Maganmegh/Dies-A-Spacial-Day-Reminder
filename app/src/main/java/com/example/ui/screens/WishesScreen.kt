package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import com.example.model.Language
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme

@Composable
fun WishesScreen(
    contacts: List<EventEntity>,
    language: Language
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = DiesTheme.colors

    var wishesLanguage by remember { mutableStateOf(language) }
    var recipientName by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var copiedNoticeMessageId by remember { mutableStateOf<String?>(null) }
    var editingMessage by remember { mutableStateOf<String?>(null) }

    val categories = if (wishesLanguage == Language.HINDI)
        WishesData.getCategoriesHi()
    else
        WishesData.getCategoriesEn()

    val filteredMessages = remember(selectedCategoryIndex, wishesLanguage) {
        val catEnList = WishesData.getCategoriesEn()
        val currentCatEn = catEnList[selectedCategoryIndex.coerceIn(0, catEnList.lastIndex)]
        if (currentCatEn == "All") {
            WishesData.messages
        } else {
            WishesData.messages.filter { it.categoryEn == currentCatEn }
        }
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

            // Wishes Language selector & Recipient customizer
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == Language.HINDI) "संदेश की भाषा:" else "WISHES LANGUAGE:",
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Toggle between Hindi & English wishes
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (wishesLanguage == Language.HINDI) colors.goldenYellow else Color.Transparent)
                                    .clickable { wishesLanguage = Language.HINDI }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "हिन्दी",
                                    color = if (wishesLanguage == Language.HINDI) Color(0xFF242A2E) else colors.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (wishesLanguage == Language.ENGLISH) colors.goldenYellow else Color.Transparent)
                                    .clickable { wishesLanguage = Language.ENGLISH }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "English",
                                    color = if (wishesLanguage == Language.ENGLISH) Color(0xFF242A2E) else colors.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Name Auto-insert TextField
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wishes_recipient_input"),
                        placeholder = {
                            Text(
                                text = if (language == Language.HINDI)
                                    "प्राप्तकर्ता का नाम (उदा. राहुल, माँ)..."
                                else
                                    "Recipient name to customize wishes...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name",
                                tint = colors.sageTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
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

                    // Quick contact chip selector if contacts available
                    if (contacts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            contacts.take(8).forEach { contact ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (recipientName == contact.name) colors.sageTeal.copy(alpha = 0.22f) else colors.surfaceVariant)
                                        .border(
                                            0.5.dp,
                                            if (recipientName == contact.name) colors.sageTeal else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { recipientName = contact.name }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = contact.name,
                                        color = if (recipientName == contact.name) colors.sageTeal else colors.textSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Category Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, title ->
                    val isSelected = selectedCategoryIndex == index
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp))
                            .background(if (isSelected) colors.sageTeal else colors.surface)
                            .border(
                                1.dp,
                                if (isSelected) colors.sageTeal else colors.surfaceBorder,
                                CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                            )
                            .clickable { selectedCategoryIndex = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("wish_cat_$index")
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else colors.textPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Wish Message Cards List
        items(filteredMessages, key = { it.id }) { message ->
            val rawText = if (wishesLanguage == Language.HINDI) message.textHi else message.textEn
            val effectiveName = if (recipientName.isNotBlank()) recipientName else (if (wishesLanguage == Language.HINDI) "प्रिय मित्र" else "Dear Friend")
            val customizedText = rawText.replace("{name}", effectiveName)
            val isCopied = copiedNoticeMessageId == message.id

            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wish_card_${message.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeonBadge(
                            text = if (language == Language.HINDI) message.categoryHi else message.categoryEn.uppercase(),
                            accentColor = colors.goldenYellow
                        )
                        if (isCopied) {
                            Text(
                                text = if (language == Language.HINDI) "कॉपी हो गया! ✓" else "COPIED! ✓",
                                color = colors.sageTeal,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = customizedText,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Edit Custom Wish Button
                        IconButton(
                            onClick = { editingMessage = customizedText },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Wish",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Copy Button
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(customizedText))
                                copiedNoticeMessageId = message.id
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surface)
                                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(6.dp))
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy Message",
                                tint = if (isCopied) colors.sageTeal else colors.primaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Direct Share Button
                        Box(
                            modifier = Modifier
                                .clip(CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                                .background(colors.sageTeal)
                                .clickable { shareText(context, customizedText) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
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

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal dialog to edit customized message before sending
    if (editingMessage != null) {
        var editedText by remember { mutableStateOf(editingMessage ?: "") }
        AlertDialog(
            onDismissRequest = { editingMessage = null },
            title = {
                Text(
                    text = if (language == Language.HINDI) "संदेश संपादित करें" else "EDIT WISH MESSAGE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryAccent,
                    fontSize = 15.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.sageTeal,
                        unfocusedBorderColor = colors.surfaceBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    minLines = 4
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        shareText(context, editedText)
                        editingMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.sageTeal, contentColor = Color.White),
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                ) {
                    Text(
                        text = if (language == Language.HINDI) "शेयर करें" else "SHARE NOW",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMessage = null }) {
                    Text(
                        text = if (language == Language.HINDI) "रद्द करें" else "CANCEL",
                        color = colors.textSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            containerColor = colors.surface,
            shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
        )
    }
}
