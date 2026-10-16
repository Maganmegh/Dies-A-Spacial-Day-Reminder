package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EventEntity
import com.example.model.Language
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.DiesTheme
import com.example.util.DuplicateStrategy
import com.example.util.ImportPreviewResult

@Composable
fun ExportImportScreen(
    events: List<EventEntity>,
    language: Language,
    isTimelineEnabled: Boolean,
    importPreview: ImportPreviewResult?,
    isExporting: Boolean,
    isImporting: Boolean,
    onLanguageChange: (Language) -> Unit,
    onToggleTimeline: (Boolean) -> Unit,
    onExportRequested: (selectedIds: Set<Long>?, includePhotos: Boolean, onReady: (Uri) -> Unit) -> Unit,
    onFileSelectedForImport: (Uri) -> Unit,
    onConfirmImport: (DuplicateStrategy, (Int) -> Unit) -> Unit,
    onClearImportPreview: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = DiesTheme.colors

    var includePhotosInExport by remember { mutableStateOf(false) }
    var exportModeAll by remember { mutableStateOf(true) } // true: all, false: custom selected
    var selectedEventIdsForExport by remember { mutableStateOf(setOf<Long>()) }
    var isSelectContactsModalOpen by remember { mutableStateOf(false) }

    var selectedDuplicateStrategy by remember { mutableStateOf(DuplicateStrategy.SKIP_DUPLICATES) }
    var lastImportSuccessCount by remember { mutableStateOf<Int?>(null) }

    // SAF Document Picker for .birthdaypack files
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            onFileSelectedForImport(uri)
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

            // Offline Privacy Guarantee Banner
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = colors.sageTeal.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Offline Guarantee",
                        tint = colors.sageTeal,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.HINDI)
                                "100% ऑफलाइन एवं निजी [ZERO CLOUD]"
                            else
                                "100% OFFLINE & PRIVATE [ZERO CLOUD]",
                            color = colors.sageTeal,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (language == Language.HINDI)
                                "आपका डेटा और तस्वीरें केवल आपके फोन में सुरक्षित रहती हैं। बिना इंटरनेट के एक फोन से दूसरे में भेजें।"
                            else
                                "Your data and photos stay solely on your device. Share birthday packs peer-to-peer without any cloud account.",
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // SECTION: EXPORT BIRTHDAYS (एक्सपोर्ट)
        item {
            Text(
                text = if (language == Language.HINDI) "जन्मदिन एक्सपोर्ट करें (.birthdaypack)" else "EXPORT BIRTHDAYS (.birthdaypack)",
                color = colors.primaryAccent,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = if (language == Language.HINDI)
                            "डेटा को .birthdaypack फाइल में निर्यात करें और ब्लूटूथ, मैसेज या फाइल शेयरिंग से किसी भी अन्य व्यक्ति को भेजें।"
                        else
                            "Package your occasions into a .birthdaypack file and send via Bluetooth, nearby share, or messaging apps.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // All vs Selected Radio options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { exportModeAll = true }
                                .padding(end = 16.dp)
                        ) {
                            RadioButton(
                                selected = exportModeAll,
                                onClick = { exportModeAll = true },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.sageTeal)
                            )
                            Text(
                                text = if (language == Language.HINDI) "सभी (${events.size})" else "All (${events.size})",
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                exportModeAll = false
                                isSelectContactsModalOpen = true
                            }
                        ) {
                            RadioButton(
                                selected = !exportModeAll,
                                onClick = {
                                    exportModeAll = false
                                    isSelectContactsModalOpen = true
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.sageTeal)
                            )
                            Text(
                                text = if (language == Language.HINDI)
                                    "चुने हुए (${selectedEventIdsForExport.size})"
                                else
                                    "Selected (${selectedEventIdsForExport.size})",
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Include Photos Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = colors.goldenYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (language == Language.HINDI) "तस्वीरें भी शामिल करें" else "Include Photos in Package",
                                    color = colors.textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (language == Language.HINDI) "पैकेज में तस्वीरें सुरक्षित पैक होंगी" else "Photos will be bundled inside package",
                                    color = colors.textMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = includePhotosInExport,
                            onCheckedChange = { includePhotosInExport = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.goldenYellow,
                                checkedTrackColor = colors.goldenYellow.copy(alpha = 0.4f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Export & Share Action Button
                    Button(
                        onClick = {
                            val targetIds = if (exportModeAll) null else selectedEventIdsForExport
                            onExportRequested(targetIds, includePhotosInExport) { contentUri ->
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/octet-stream"
                                    putExtra(Intent.EXTRA_STREAM, contentUri)
                                    putExtra(Intent.EXTRA_SUBJECT, "Dies Birthday Pack")
                                    putExtra(Intent.EXTRA_TEXT, "Here is my birthday and memory calendar backup file (.birthdaypack) for the Dies app!")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                val chooser = Intent.createChooser(sendIntent, "Share Birthdaypack via")
                                context.startActivity(chooser)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_share_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.sageTeal,
                            contentColor = Color.White
                        ),
                        shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                        enabled = !isExporting && (exportModeAll || selectedEventIdsForExport.isNotEmpty())
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export & Share",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (language == Language.HINDI) "एक्सपोर्ट व शेयर करें" else "EXPORT & SHARE FILE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // SECTION: IMPORT BIRTHDAYS (इम्पोर्ट)
        item {
            Text(
                text = if (language == Language.HINDI) "जन्मदिन इम्पोर्ट करें (.birthdaypack)" else "IMPORT BIRTHDAYS (.birthdaypack)",
                color = colors.goldenYellow,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = if (language == Language.HINDI)
                            "किसी दूसरे फोन या बैकअप से प्राप्त .birthdaypack फाइल को चुनें। इम्पोर्ट से पहले प्रीव्यू और डुप्लिकेट प्रबंधन उपलब्ध है।"
                        else
                            "Select a .birthdaypack file received from a friend or previous backup. Preview and duplicate resolution are provided.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            documentPickerLauncher.launch(arrayOf("*/*"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_file_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.goldenYellow,
                            contentColor = Color(0xFF242A2E)
                        ),
                        shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                        enabled = !isImporting
                    ) {
                        if (isImporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF242A2E), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = "Choose File",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (language == Language.HINDI) "फाइल चुनें (.birthdaypack)" else "SELECT FILE TO IMPORT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    if (lastImportSuccessCount != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == Language.HINDI)
                                "✓ $lastImportSuccessCount रिकॉर्ड्स सफलतापूर्वक इम्पोर्ट किए गए!"
                            else
                                "✓ Successfully imported $lastImportSuccessCount records!",
                            color = colors.sageTeal,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // SECTION: APP PREFERENCES & MEMORY TIMELINE
        item {
            Text(
                text = if (language == Language.HINDI) "ऐप सेटिंग्स" else "APP SETTINGS",
                color = colors.textSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Language Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = colors.sageTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.HINDI) "ऐप की भाषा" else "App Interface Language",
                                    color = colors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (language == Language.HINDI) "वर्तमान: हिन्दी" else "Current: English",
                                    color = colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (language == Language.HINDI) colors.sageTeal else Color.Transparent)
                                    .clickable { onLanguageChange(Language.HINDI) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "हिन्दी",
                                    color = if (language == Language.HINDI) Color.White else colors.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (language == Language.ENGLISH) colors.sageTeal else Color.Transparent)
                                    .clickable { onLanguageChange(Language.ENGLISH) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "English",
                                    color = if (language == Language.ENGLISH) Color.White else colors.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Memory Timeline Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Timeline",
                                tint = colors.goldenYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.HINDI) "मेमोरी टाइमलाइन" else "Memory Timeline Feature",
                                    color = colors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (language == Language.HINDI)
                                        "संपर्कों के प्रोफ़ाइल में पुराने वर्षों की यादें और तस्वीरें दिखाएं"
                                    else
                                        "Organize past years memories and photos per contact",
                                    color = colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isTimelineEnabled,
                            onCheckedChange = onToggleTimeline,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.sageTeal,
                                checkedTrackColor = colors.sageTeal.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }

        // SECTION: CREATOR & COPYRIGHT (डेवलपर एवं कॉपीराइट)
        item {
            Text(
                text = if (language == Language.HINDI) "डेवलपर एवं कॉपीराइट" else "CREATOR & COPYRIGHT",
                color = colors.primaryAccent,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_copyright_card"),
                borderColor = colors.goldenYellow.copy(alpha = 0.5f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Creator profile row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InstagramIcon(size = 46)

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Magan Meghwal",
                                    color = colors.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                NeonBadge(
                                    text = if (language == Language.HINDI) "डेवलपर" else "CREATOR",
                                    accentColor = colors.goldenYellow
                                )
                            }
                            Text(
                                text = "@maganmegh",
                                color = colors.sageTeal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (language == Language.HINDI)
                                    "ऐप निर्माता एवं सॉफ्टवेयर डिज़ाइनर"
                                else
                                    "App Creator & Software Designer",
                                color = colors.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Friendly message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (language == Language.HINDI)
                                "Dies ऐप का उपयोग करने के लिए आपका धन्यवाद! नए फीचर्स के सुझाव, सहयोग या बातचीत के लिए मुझे इंस्टाग्राम पर जरूर फॉलो करें।"
                            else
                                "Thank you for using Dies! If you enjoy the app or have feedback & feature ideas, connect with me on Instagram.",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    // Action buttons: Follow on Instagram & Copy handle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Follow button
                        Button(
                            onClick = {
                                val instaWebUri = Uri.parse("https://instagram.com/maganmegh")
                                val instaAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/maganmegh")).apply {
                                    setPackage("com.instagram.android")
                                }
                                try {
                                    context.startActivity(instaAppIntent)
                                } catch (e: Exception) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, instaWebUri))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("follow_instagram_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE1306C),
                                contentColor = Color.White
                            ),
                            shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Follow",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.HINDI) "फॉलो करें @maganmegh" else "FOLLOW @MAGANMEGH",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // Copy username button
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("@maganmegh"))
                                Toast.makeText(
                                    context,
                                    if (language == Language.HINDI)
                                        "@maganmegh क्लिपबोर्ड पर कॉपी हो गया!"
                                    else
                                        "Instagram @maganmegh copied to clipboard!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("copy_instagram_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.textPrimary
                            ),
                            shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Handle",
                                modifier = Modifier.size(16.dp),
                                tint = colors.sageTeal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.HINDI) "कॉपी" else "COPY",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Divider and Copyright Notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.surfaceBorder)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (language == Language.HINDI)
                                "© 2026 मगन मेघवाल (@maganmegh) • सर्वाधिकार सुरक्षित"
                            else
                                "© 2026 Magan Meghwal (@maganmegh) • All Rights Reserved",
                            color = colors.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Dies: Smart Birthday Reminder & Personal Memory Calendar",
                            color = colors.textMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Version 1.0.0 • 100% Offline • Zero Cloud • Ad-Free",
                            color = colors.sageTeal,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal Dialog: Import Preview & Duplicate Strategy
    if (importPreview != null) {
        AlertDialog(
            onDismissRequest = onClearImportPreview,
            title = {
                Text(
                    text = if (language == Language.HINDI) "इम्पोर्ट प्रीव्यू (.birthdaypack)" else "IMPORT PREVIEW",
                    color = colors.goldenYellow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                if (!importPreview.isValid) {
                    Text(
                        text = "Error: ${importPreview.errorMessage ?: "Invalid format"}",
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "कुल रिकॉर्ड्स:" else "Total in File:",
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${importPreview.totalInFile}",
                                color = colors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "नए रिकॉर्ड्स:" else "New Records:",
                                color = colors.sageTeal,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${importPreview.newCount}",
                                color = colors.sageTeal,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == Language.HINDI) "पहले से मौजूद (डुप्लिकेट):" else "Duplicates Found:",
                                color = colors.goldenYellow,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${importPreview.duplicateCount}",
                                color = colors.goldenYellow,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (importPreview.sampleNames.isNotEmpty()) {
                            Text(
                                text = "${if (language == Language.HINDI) "नमूना नाम:" else "Sample:"} ${importPreview.sampleNames.joinToString(", ")}...",
                                color = colors.textMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Duplicate Resolution Strategy Radios
                        Text(
                            text = if (language == Language.HINDI) "डुप्लिकेट प्रबंधन:" else "Duplicate Strategy:",
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDuplicateStrategy = DuplicateStrategy.SKIP_DUPLICATES }
                            ) {
                                RadioButton(
                                    selected = selectedDuplicateStrategy == DuplicateStrategy.SKIP_DUPLICATES,
                                    onClick = { selectedDuplicateStrategy = DuplicateStrategy.SKIP_DUPLICATES },
                                    colors = RadioButtonDefaults.colors(selectedColor = colors.sageTeal)
                                )
                                Text(
                                    text = if (language == Language.HINDI) "डुप्लिकेट छोड़ें (केवल नए जोड़ें)" else "Skip duplicates (add new only)",
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDuplicateStrategy = DuplicateStrategy.OVERWRITE_EXISTING }
                            ) {
                                RadioButton(
                                    selected = selectedDuplicateStrategy == DuplicateStrategy.OVERWRITE_EXISTING,
                                    onClick = { selectedDuplicateStrategy = DuplicateStrategy.OVERWRITE_EXISTING },
                                    colors = RadioButtonDefaults.colors(selectedColor = colors.sageTeal)
                                )
                                Text(
                                    text = if (language == Language.HINDI) "पुराने को अपडेट करें" else "Update existing records",
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDuplicateStrategy = DuplicateStrategy.ADD_ALL_AS_NEW }
                            ) {
                                RadioButton(
                                    selected = selectedDuplicateStrategy == DuplicateStrategy.ADD_ALL_AS_NEW,
                                    onClick = { selectedDuplicateStrategy = DuplicateStrategy.ADD_ALL_AS_NEW },
                                    colors = RadioButtonDefaults.colors(selectedColor = colors.sageTeal)
                                )
                                Text(
                                    text = if (language == Language.HINDI) "सब कुछ जोड़ें" else "Add all as new",
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (importPreview.isValid) {
                    Button(
                        onClick = {
                            onConfirmImport(selectedDuplicateStrategy) { count ->
                                lastImportSuccessCount = count
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.goldenYellow,
                            contentColor = Color(0xFF242A2E)
                        ),
                        shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                    ) {
                        Text(
                            text = if (language == Language.HINDI) "इम्पोर्ट की पुष्टि करें" else "CONFIRM IMPORT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onClearImportPreview) {
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

    // Modal Dialog: Select Specific Contacts for Export
    if (isSelectContactsModalOpen) {
        AlertDialog(
            onDismissRequest = { isSelectContactsModalOpen = false },
            title = {
                Text(
                    text = if (language == Language.HINDI) "एक्सपोर्ट हेतु संपर्कों का चयन" else "SELECT CONTACTS TO EXPORT",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(events, key = { "export_sel_${it.id}" }) { ev ->
                        val isSelected = ev.id in selectedEventIdsForExport
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) colors.sageTeal.copy(alpha = 0.2f) else colors.surfaceVariant)
                                .clickable {
                                    selectedEventIdsForExport = if (isSelected) {
                                        selectedEventIdsForExport - ev.id
                                    } else {
                                        selectedEventIdsForExport + ev.id
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = ev.name,
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = colors.sageTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isSelectContactsModalOpen = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.sageTeal,
                        contentColor = Color.White
                    ),
                    shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp)
                ) {
                    Text(
                        text = if (language == Language.HINDI) "चयन पूरा करें (${selectedEventIdsForExport.size})" else "DONE (${selectedEventIdsForExport.size})",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = colors.surface,
            shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
        )
    }
}

@Composable
fun InstagramIcon(modifier: Modifier = Modifier, size: Int = 24) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.28).dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF833AB4),
                        Color(0xFFFD1D1D),
                        Color(0xFFFCB045)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outline camera box
        Box(
            modifier = Modifier
                .size((size * 0.62).dp)
                .clip(RoundedCornerShape((size * 0.18).dp))
                .border(
                    width = 1.6.dp,
                    color = Color.White,
                    shape = RoundedCornerShape((size * 0.18).dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Center lens
            Box(
                modifier = Modifier
                    .size((size * 0.28).dp)
                    .clip(CircleShape)
                    .border(1.4.dp, Color.White, CircleShape)
            )
        }
        // Top right flash dot
        Box(
            modifier = Modifier
                .size((size * 0.09).dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopEnd)
                .padding(top = (size * 0.22).dp, end = (size * 0.22).dp)
        )
    }
}

