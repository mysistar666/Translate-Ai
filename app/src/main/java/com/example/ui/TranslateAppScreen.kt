package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.api.MultiTranslationResult
import com.example.api.TranslationResult
import com.example.data.TranslationRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Professional Polish Color Theme Palette Definitions
data class AppColors(
    val isDark: Boolean,
    val slateBackground: Color,
    val cardBackground: Color,
    val primaryIndigo: Color,
    val secondaryIndigo: Color,
    val indigo50: Color,
    val indigo50_50: Color,
    val indigo100: Color,
    val indigo900: Color,
    val slate900: Color,
    val slate800: Color,
    val slate600: Color,
    val slate500: Color,
    val slate400: Color,
    val slate300: Color,
    val slate200: Color,
    val slate100: Color,
    val correctedBoxGreen: Color,
    val correctedBoxGreenBg: Color,
    val correctedBoxGreenText: Color,
    val softPurple: Color,
    val softPurpleBg: Color
)

val LightAppColors = AppColors(
    isDark = false,
    slateBackground = Color(0xFFF7F9FC),
    cardBackground = Color.White,
    primaryIndigo = Color(0xFF4F46E5),
    secondaryIndigo = Color(0xFF3F37C9),
    indigo50 = Color(0xFFEEF2FF),
    indigo50_50 = Color(0xFFF5F7FF),
    indigo100 = Color(0xFFE0E7FF),
    indigo900 = Color(0xFF312E81),
    slate900 = Color(0xFF0F172A),
    slate800 = Color(0xFF1E293B),
    slate600 = Color(0xFF475569),
    slate500 = Color(0xFF64748B),
    slate400 = Color(0xFF94A3B8),
    slate300 = Color(0xFFCBD5E1),
    slate200 = Color(0xFFE2E8F0),
    slate100 = Color(0xFFF1F5F9),
    correctedBoxGreen = Color(0xFF10B981),
    correctedBoxGreenBg = Color(0xFFECFDF5),
    correctedBoxGreenText = Color(0xFF047857),
    softPurple = Color(0xFFBF5AF2),
    softPurpleBg = Color(0xFFFAF5FF)
)

val DarkAppColors = AppColors(
    isDark = true,
    slateBackground = Color(0xFF0B0F19), // smooth deep dark space background
    cardBackground = Color(0xFF161F30), // rich elevated card slate surface
    primaryIndigo = Color(0xFF818CF8), // brighter indigo for high dark-mode contrast
    secondaryIndigo = Color(0xFF6366F1),
    indigo50 = Color(0xFF1E2950), // soft navy-indigo accent box background
    indigo50_50 = Color(0xFF1A2346),
    indigo100 = Color(0xFF2E3D6E),
    indigo900 = Color(0xFFEEF2FF), // white-indigo for on-card light texts
    slate900 = Color(0xFFF8FAFC), // dynamic white-grey primary text
    slate800 = Color(0xFFF1F5F9), // elevated secondary headings
    slate600 = Color(0xFFE2E8F0), // readable labels in dark mode
    slate500 = Color(0xFF94A3B8), // placeholder captions
    slate400 = Color(0xFF64748B), // disabled/unselected
    slate300 = Color(0xFF475569), // border lines
    slate200 = Color(0xFF334155), // light-bordered outlines
    slate100 = Color(0xFF1E293B), // chip-type background
    correctedBoxGreen = Color(0xFF34D399),
    correctedBoxGreenBg = Color(0xFF064E3B),
    correctedBoxGreenText = Color(0xFFA7F3D0),
    softPurple = Color(0xFFD8B4FE),
    softPurpleBg = Color(0xFF3B0764)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

private val SlateBackground: Color @Composable get() = LocalAppColors.current.slateBackground
private val CardBackground: Color @Composable get() = LocalAppColors.current.cardBackground
private val PrimaryIndigo: Color @Composable get() = LocalAppColors.current.primaryIndigo
private val SecondaryIndigo: Color @Composable get() = LocalAppColors.current.secondaryIndigo
private val Indigo50: Color @Composable get() = LocalAppColors.current.indigo50
private val Indigo50_50: Color @Composable get() = LocalAppColors.current.indigo50_50
private val Indigo100: Color @Composable get() = LocalAppColors.current.indigo100
private val Indigo900: Color @Composable get() = LocalAppColors.current.indigo900

private val Slate900: Color @Composable get() = LocalAppColors.current.slate900
private val Slate800: Color @Composable get() = LocalAppColors.current.slate800
private val Slate600: Color @Composable get() = LocalAppColors.current.slate600
private val Slate500: Color @Composable get() = LocalAppColors.current.slate500
private val Slate400: Color @Composable get() = LocalAppColors.current.slate400
private val Slate300: Color @Composable get() = LocalAppColors.current.slate300
private val Slate200: Color @Composable get() = LocalAppColors.current.slate200
private val Slate100: Color @Composable get() = LocalAppColors.current.slate100

private val CorrectedBoxGreen: Color @Composable get() = LocalAppColors.current.correctedBoxGreen
private val CorrectedBoxGreenBg: Color @Composable get() = LocalAppColors.current.correctedBoxGreenBg
private val CorrectedBoxGreenText: Color @Composable get() = LocalAppColors.current.correctedBoxGreenText
private val SoftPurple: Color @Composable get() = LocalAppColors.current.softPurple
private val SoftPurpleBg: Color @Composable get() = LocalAppColors.current.softPurpleBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslateAppScreen(
    viewModel: TranslationViewModel,
    onSpeak: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appColors = if (isDarkMode) DarkAppColors else LightAppColors

    var showDownloadDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        if (showDownloadDialog) {
            AlertDialog(
                onDismissRequest = { showDownloadDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Download Full App",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "আপনার ফোনে সম্পূর্ণ TranslateAI অ্যান্ড্রয়েড অ্যাপ ডাউনলোড করতে নিচের পদক্ষেপগুলো অনুসরণ করুন:",
                            fontSize = 13.sp,
                            color = Slate600,
                            fontWeight = FontWeight.Medium
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate100),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("১.", fontWeight = FontWeight.Bold, color = PrimaryIndigo, fontSize = 13.sp)
                                    Text(
                                        "ক্রোম/ব্রাউজারে ওপরে ডানদিকের কোণায় Settings (গিয়ার আইকন) বা ড্রপডাউনে ক্লিক করুন।",
                                        fontSize = 13.sp,
                                        color = Slate800
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("২.", fontWeight = FontWeight.Bold, color = PrimaryIndigo, fontSize = 13.sp)
                                    Text(
                                        "সেখান থেকে 'Generate APK' সিলেক্ট করুন। কিছুক্ষণের মধ্যে একটি ইনস্টলযোগ্য APK ফাইল তৈরি হয়ে যাবে যা আপনি ফোনে ডাউনলোড করতে পারবেন।",
                                        fontSize = 13.sp,
                                        color = Slate800
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("৩.", fontWeight = FontWeight.Bold, color = PrimaryIndigo, fontSize = 13.sp)
                                    Text(
                                        "পুরো প্রোজেক্টের সোর্স কোড ডাউনলোড করার জন্য ওপরের 'ZIP' বাটনে ক্লিক করতে পারেন।",
                                        fontSize = 13.sp,
                                        color = Slate800
                                    )
                                }
                            }
                        }

                        Text(
                            text = "If you are hosting a web version or want the code files, we have provided premium, fully-responsive CSS styled code for index.html, style.css, and script.js in your workspace!",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDownloadDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text("ঠিক আছে", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = CardBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }

        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier.background(CardBackground),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(color = Slate100, modifier = Modifier.fillMaxWidth())
                    NavigationBar(
                        containerColor = CardBackground,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(72.dp)
                            .padding(top = 8.dp)
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Translate, contentDescription = "Translator") },
                            label = { Text("Single", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.testTag("single_translate_tab")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.Layers, contentDescription = "Multi-Translate") },
                            label = { Text("Multi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.testTag("multi_translate_tab")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.History, contentDescription = "History") },
                            label = { Text("Activity", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.testTag("history_tab")
                        )
                    }
                    Text(
                        text = "Presented by MUKLES",
                        color = Slate500,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .navigationBarsPadding()
                    )
                }
            },
            containerColor = SlateBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                // Elegant App Title Header with Theme Toggle and Download options
                HeaderBar(
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleDarkMode() },
                    onDownloadClick = { showDownloadDialog = true }
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        0 -> SingleTranslateTab(viewModel, onSpeak)
                        1 -> MultiTranslateTab(viewModel, onSpeak)
                        2 -> HistoryTab(viewModel, onSpeak)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    .background(
                        color = PrimaryIndigo,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "TranslateAI",
                    color = Slate900,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Intelligence Translation Engine",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.testTag("download_app_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download Full App",
                    tint = PrimaryIndigo
                )
            }

            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark/Light Mode",
                    tint = if (isDarkMode) PrimaryIndigo else Slate600
                )
            }
        }
    }
    // Thin separator line mimicking the bottom-border
    Divider(color = Slate100, thickness = 1.dp)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SingleTranslateTab(
    viewModel: TranslationViewModel,
    onSpeak: (String, String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val inputText by viewModel.singleInputText.collectAsStateWithLifecycle()
    val sourceLang by viewModel.singleSourceLang.collectAsStateWithLifecycle()
    val targetLang by viewModel.singleTargetLang.collectAsStateWithLifecycle()
    val tone by viewModel.selectedTone.collectAsStateWithLifecycle()
    val correctGrammar by viewModel.correctGrammar.collectAsStateWithLifecycle()
    val isTranslating by viewModel.isTranslatingSingle.collectAsStateWithLifecycle()
    val singleResult by viewModel.singleResult.collectAsStateWithLifecycle()

    var showSourceDropdown by remember { mutableStateOf(false) }
    var showTargetDropdown by remember { mutableStateOf(false) }

    // Speech Recognizer intent Setup
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                viewModel.updateSingleInput(spokenText)
            }
        }
    }

    val triggerSpeechInput = {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Source/Target Dropdown Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Source Dropdown Selector
                    Box(modifier = Modifier.weight(1f)) {
                        TextButton(
                            onClick = { showSourceDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${sourceLang.flag} ${sourceLang.name}",
                                    color = PrimaryIndigo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
                            }
                        }
                        DropdownMenu(
                            expanded = showSourceDropdown,
                            onDismissRequest = { showSourceDropdown = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            viewModel.supportedLanguages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.name}", color = Slate900, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setSingleSourceLang(lang)
                                        showSourceDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Swap languages button (only if source is not Auto-Detect)
                    IconButton(
                        onClick = {
                            if (sourceLang.code != "auto") {
                                val s = sourceLang
                                  val t = targetLang
                                viewModel.setSingleSourceLang(t)
                                viewModel.setSingleTargetLang(s)
                            } else {
                                Toast.makeText(context, "Cannot swap with Auto-Detect", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = sourceLang.code != "auto"
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = if (sourceLang.code != "auto") PrimaryIndigo else Slate400,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Target Language Selector
                    Box(modifier = Modifier.weight(1f)) {
                        TextButton(
                            onClick = { showTargetDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${targetLang.flag} ${targetLang.name}",
                                    color = Slate800,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
                            }
                        }
                        DropdownMenu(
                            expanded = showTargetDropdown,
                            onDismissRequest = { showTargetDropdown = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            viewModel.supportedLanguages.filter { it.code != "auto" }.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.name}", color = Slate900, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setSingleTargetLang(lang)
                                        showTargetDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Text Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Source text", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        Row {
                            IconButton(onClick = triggerSpeechInput) {
                                Icon(Icons.Default.Mic, contentDescription = "Speak Text Input", tint = PrimaryIndigo)
                            }
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSingleInput() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Input", tint = Slate500)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateSingleInput(it) },
                        placeholder = { Text("Enter text here...", color = Slate400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("input_text_field"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Paste Action
                        TextButton(
                            onClick = {
                                val pasteText = clipboardManager.getText()?.text
                                if (!pasteText.isNullOrEmpty()) {
                                    viewModel.updateSingleInput(pasteText)
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paste From Clipboard", color = PrimaryIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "${inputText.length} chars",
                            color = Slate500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Translation Tone Selector (Horizontal Filters)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Tone", color = Slate800, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.tones) { itemTone ->
                        val selected = tone == itemTone
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    color = if (selected) PrimaryIndigo else Slate100
                                )
                                .border(
                                    border = if (selected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Slate200),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setSelectedTone(itemTone) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = itemTone,
                                color = if (selected) Color.White else Slate600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Options: Pre-correct Grammar first Checkbox
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleGrammarCorrection() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = correctGrammar,
                        onCheckedChange = { viewModel.toggleGrammarCorrection() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryIndigo,
                            checkmarkColor = Color.White,
                            uncheckedColor = Slate400
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pre-Correct Grammar", color = Slate900, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Analyzes and fixes writing errors before generating the final translation.", color = Slate500, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(SoftPurpleBg, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, SoftPurple.copy(alpha = 0.2f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("AI", color = SoftPurple, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Full Width Translate Button
        item {
            Button(
                onClick = { viewModel.translateSingleText() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("translate_button"),
                enabled = inputText.isNotBlank() && !isTranslating,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIndigo,
                    disabledContainerColor = PrimaryIndigo.copy(alpha = 0.45f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                )
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("AI Translating...", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Language, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translate Now", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Result Container
        item {
            Crossfade(targetState = singleResult, label = "ResultFade") { result ->
                when (result) {
                    is TranslationResult.Success -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            // Display corrected grammar log if enabled and text was modified
                            if (correctGrammar && result.corrected != result.original) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CorrectedBoxGreenBg),
                                    border = BorderStroke(1.dp, CorrectedBoxGreen.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = CorrectedBoxGreen, modifier = Modifier.size(16.dp))
                                            Text("Grammar Corrected", color = CorrectedBoxGreenText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(result.corrected, color = Slate800, fontSize = 13.sp, lineHeight = 18.sp)
                                    }
                                }
                            }

                            // Translation card display
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Indigo50_50),
                                border = BorderStroke(1.dp, Indigo100),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${targetLang.flag} ${targetLang.name} (${tone})",
                                            color = Indigo900,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            // Speak
                                            IconButton(onClick = { onSpeak(result.translated, targetLang.name) }) {
                                                Icon(Icons.Default.VolumeUp, contentDescription = "Vocal Speak", tint = Slate600)
                                            }
                                            // Copy
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(result.translated))
                                                    Toast.makeText(context, "Translation copied!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Translation", tint = Slate600)
                                            }
                                            // Share
                                            IconButton(
                                                onClick = {
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_TEXT, result.translated)
                                                    }
                                                    context.startActivity(Intent.createChooser(shareIntent, "Share translation via"))
                                                }
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate600)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = result.translated,
                                        color = Indigo900,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 26.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    is TranslationResult.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Error icon", tint = Color(0xFFDC2626))
                                Text(
                                    text = result.message,
                                    color = Color(0xFF991B1B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    null -> {
                        // Empty state placeholder with an icon
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = Slate300,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "Provide text above and generate precise translation instantly.",
                                color = Slate500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiTranslateTab(
    viewModel: TranslationViewModel,
    onSpeak: (String, String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val multiInputText by viewModel.multiInputText.collectAsStateWithLifecycle()
    val multiSourceLang by viewModel.multiSourceLang.collectAsStateWithLifecycle()
    val selectedTargets by viewModel.multiTargetLanguages.collectAsStateWithLifecycle()
    val multiTone by viewModel.multiTone.collectAsStateWithLifecycle()
    val isTranslating by viewModel.isTranslatingMulti.collectAsStateWithLifecycle()
    val multiResult by viewModel.multiResult.collectAsStateWithLifecycle()

    var showSourceDropdown by remember { mutableStateOf(false) }
    var showAddLangDialog by remember { mutableStateOf(false) }

    // Speech setup for multi
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                viewModel.updateMultiInput(spokenText)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Source selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Source: ", color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Box {
                            TextButton(onClick = { showSourceDropdown = true }) {
                                Text("${multiSourceLang.flag} ${multiSourceLang.name}", color = PrimaryIndigo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
                            }
                            DropdownMenu(
                                expanded = showSourceDropdown,
                                onDismissRequest = { showSourceDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.supportedLanguages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text("${lang.flag} ${lang.name}", color = Slate900, fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            viewModel.setMultiSourceLang(lang)
                                            showSourceDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(Indigo50, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.2f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Multi-Mode", color = PrimaryIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Input Area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Source text", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        Row {
                            IconButton(onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                    }
                                    speechRecognizerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Voice input not supported", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Mic, contentDescription = "Speech input", tint = PrimaryIndigo)
                            }
                            if (multiInputText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearMultiInput() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate500)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = multiInputText,
                        onValueChange = { viewModel.updateMultiInput(it) },
                        placeholder = { Text("Enter text to translate to multiple languages simultaneously...", color = Slate400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                val text = clipboardManager.getText()?.text
                                if (!text.isNullOrEmpty()) viewModel.updateMultiInput(text)
                            }
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paste text", color = PrimaryIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Text("${multiInputText.length} chars", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Tones control list
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Requested Tone", color = Slate800, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.tones) { tone ->
                        val active = multiTone == tone
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    color = if (active) PrimaryIndigo else Slate100
                                )
                                .border(
                                    border = if (active) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Slate200),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setMultiTone(tone) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(tone, color = if (active) Color.White else Slate600, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Target languages select dashboard
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target Languages", color = Slate800, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { showAddLangDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add/Remove", color = PrimaryIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (selectedTargets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No targets selected yet. Please click Add/Remove.", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedTargets.forEach { lang ->
                            Box(
                                modifier = Modifier
                                    .background(Slate100, RoundedCornerShape(12.dp))
                                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.toggleMultiTargetLanguage(lang) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${lang.flag} ${lang.name}", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Clear, contentDescription = "Remove", tint = Slate500, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trigger translate Button
        item {
            Button(
                onClick = { viewModel.translateMultiText() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("multi_translate_button"),
                enabled = multiInputText.isNotBlank() && !isTranslating && selectedTargets.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIndigo,
                    disabledContainerColor = PrimaryIndigo.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Translating to ${selectedTargets.size} languages...", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translate Concurrently", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Multi translations outputs stack
        item {
            Crossfade(targetState = multiResult, label = "MultiFade") { result ->
                when (result) {
                    is MultiTranslationResult.Success -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Text("Translations Generated:", color = Slate800, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            
                            result.translations.forEach { (language, translation) ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Indigo50_50),
                                    border = BorderStroke(1.dp, Indigo100),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val flag = viewModel.supportedLanguages.firstOrNull { it.name.equals(language, ignoreCase = true) }?.flag ?: "🌐"
                                            Text(
                                                text = "$flag $language",
                                                color = Indigo900,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = { onSpeak(translation, language) }) {
                                                    Icon(Icons.Default.VolumeUp, contentDescription = "Play voice audio", tint = Slate600, modifier = Modifier.size(18.dp))
                                                }
                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(translation))
                                                        Toast.makeText(context, "$language Translation copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = Slate600, modifier = Modifier.size(18.dp))
                                                }
                                                IconButton(
                                                    onClick = {
                                                        val share = Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(Intent.EXTRA_TEXT, "[$language Translation] $translation")
                                                        }
                                                        context.startActivity(Intent.createChooser(share, "Share"))
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate600, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = translation,
                                            color = Indigo900,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is MultiTranslationResult.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = result.message,
                                color = Color(0xFF991B1B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    null -> {
                        // Empty state placeholder with an icon
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = Slate300,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "Choose target languages to compare parallel translations on a single workstation.",
                                color = Slate500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to add/remove multiple languages
    if (showAddLangDialog) {
        Dialog(onDismissRequest = { showAddLangDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Slate200),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Target Languages",
                        color = Slate900,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Text(
                        "Toggle checklist to choose languages for multi-mode translation.",
                        color = Slate500,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.verticalScroll(scrollState)) {
                            // Filter out "Auto-Detect" as a target language setting
                            viewModel.supportedLanguages.filter { it.code != "auto" }.forEach { lang ->
                                val active = selectedTargets.contains(lang)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.toggleMultiTargetLanguage(lang) }
                                        .padding(vertical = 4.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = active,
                                        onCheckedChange = { viewModel.toggleMultiTargetLanguage(lang) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = PrimaryIndigo,
                                            checkmarkColor = Color.White,
                                            uncheckedColor = Slate400
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("${lang.flag} ${lang.name}", color = Slate800, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddLangDialog = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text("Apply Selection (${selectedTargets.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    viewModel: TranslationViewModel,
    onSpeak: (String, String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val favoritesList by viewModel.favoritesList.collectAsStateWithLifecycle()

    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val currentRecords = if (showFavoritesOnly) favoritesList else historyList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search and Filter row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search translations...", color = Slate400) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Slate500)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Slate900,
                unfocusedTextColor = Slate900,
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = Slate200,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(16.dp)
        )

        // Segmented Control filter: All Vs Favorites
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(Slate100, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // All History Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showFavoritesOnly = false }
                        .background(
                            color = if (!showFavoritesOnly) PrimaryIndigo else Color.Transparent
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "All History (${historyList.size})",
                        color = if (!showFavoritesOnly) Color.White else Slate600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Favorites Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showFavoritesOnly = true }
                        .background(
                            color = if (showFavoritesOnly) PrimaryIndigo else Color.Transparent
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Starred (${favoritesList.size})",
                        color = if (showFavoritesOnly) Color.White else Slate600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Clear history button
            if (!showFavoritesOnly && historyList.isNotEmpty()) {
                IconButton(onClick = { showClearConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear all history", tint = Color(0xFFEF4444))
                }
            }
        }

        // Lazy history list
        if (currentRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (showFavoritesOnly) Icons.Default.FavoriteBorder else Icons.Default.History,
                        contentDescription = "Empty",
                        tint = Slate300,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (showFavoritesOnly) "No translations saved. Star translations on the translation tab to save them." else "History is clear. Translations you perform will stack here.",
                        color = Slate500,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(currentRecords, key = { it.id }) { record ->
                    HistoryItemCard(
                        record = record,
                        onSpeak = { onSpeak(record.translatedText, record.targetLang) },
                        onFavoriteToggle = { viewModel.toggleFavorite(record) },
                        onDelete = { viewModel.deleteHistoricalRecord(record.id) },
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(record.translatedText))
                            Toast.makeText(context, "Translation copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Clear all confirmation dialog
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All History?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all records from your translation history database. Saved stars will be preserved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirm = false
                    }
                ) {
                    Text("Clear All", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = Slate500, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardBackground,
            titleContentColor = Slate900,
            textContentColor = Slate600
        )
    }
}

@Composable
fun HistoryItemCard(
    record: TranslationRecord,
    onSpeak: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, Slate200),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Source -> Target & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = record.sourceLang,
                        color = Slate500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = record.targetLang,
                        color = PrimaryIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (record.tone.isNotBlank() && record.tone != "Standard") {
                        Box(
                            modifier = Modifier
                                .background(Indigo50, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.2f)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(record.tone, color = PrimaryIndigo, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Date stamp
                val dateStr = remember(record.timestamp) {
                    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    sdf.format(Date(record.timestamp))
                }
                Text(
                    text = dateStr,
                    color = Slate400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Original input text preview
            Text(
                text = record.originalText,
                color = Slate500,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Translated text
            Text(
                text = record.translatedText,
                color = Slate900,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick actions line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button (left)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Actions row (right)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Play voice audio", tint = Slate600, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = Slate600, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, record.translatedText)
                            }
                            context.startActivity(Intent.createChooser(share, "Share"))
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate600, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (record.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Star favorite",
                            tint = if (record.isFavorite) Color(0xFFEF4444) else Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
