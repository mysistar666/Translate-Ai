package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.TranslationRepository
import com.example.ui.TranslateAppScreen
import com.example.ui.TranslationViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize DB and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TranslationRepository(database.translationDao())

        // 2. Wire up ViewModel with Factory
        val factory = ViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[TranslationViewModel::class.java]

        // 3. Initialize native Text-to-Speech Engine
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                Log.d(TAG, "Native TextToSpeech successfully initialized.")
            } else {
                Log.e(TAG, "Failed to initialize native TextToSpeech engine.")
            }
        }

        // 4. Render Layout with edge-to-edge scaffolding
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TranslateAppScreen(
                        viewModel = viewModel,
                        onSpeak = { text, targetLangName ->
                            speakText(text, targetLangName)
                        }
                    )
                }
            }
        }
    }

    private fun speakText(text: String, languageName: String) {
        if (!isTtsInitialized) {
            Log.w(TAG, "TTS requested but not yet initialized.")
            return
        }

        // Map English language name to Java Locale
        val locale = when (languageName.lowercase(Locale.ROOT)) {
            "english" -> Locale.ENGLISH
            "spanish" -> Locale("es")
            "french" -> Locale.FRENCH
            "german" -> Locale.GERMAN
            "hindi" -> Locale("hi")
            "bengali" -> Locale("bn")
            "urdu" -> Locale("ur")
            "arabic" -> Locale("ar")
            "portuguese" -> Locale("pt")
            "russian" -> Locale("ru")
            "chinese" -> Locale.CHINESE
            "japanese" -> Locale.JAPANESE
            "korean" -> Locale.KOREAN
            "turkish" -> Locale("tr")
            "indonesian" -> Locale("id")
            "thai" -> Locale("th")
            "vietnamese" -> Locale("vi")
            "italian" -> Locale.ITALIAN
            "dutch" -> Locale.GERMAN // fallback or generic
            "swedish" -> Locale("sv")
            else -> Locale.getDefault()
        }

        try {
            tts.language = locale
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TranslateSpeechOutput")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio vocal playback", e)
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
