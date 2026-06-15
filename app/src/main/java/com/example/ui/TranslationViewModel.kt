package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.api.MultiTranslationResult
import com.example.api.TranslationResult
import com.example.data.AppDatabase
import com.example.data.TranslationRecord
import com.example.data.TranslationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

data class Language(val name: String, val code: String, val flag: String) {
    override fun toString(): String = "$flag $name"
}

class TranslationViewModel(
    application: Application,
    private val repository: TranslationRepository
) : AndroidViewModel(application) {

    // Language list configuration
    val supportedLanguages = listOf(
        Language("Auto-Detect", "auto", "🔍"),
        Language("English", "en", "🇺🇸"),
        Language("Spanish", "es", "🇪🇸"),
        Language("French", "fr", "🇫🇷"),
        Language("German", "de", "🇩🇪"),
        Language("Hindi", "hi", "🇮🇳"),
        Language("Bengali", "bn", "🇧🇩"),
        Language("Urdu", "ur", "🇵🇰"),
        Language("Arabic", "ar", "🇸🇦"),
        Language("Portuguese", "pt", "🇵🇹"),
        Language("Russian", "ru", "🇷🇺"),
        Language("Chinese", "zh", "🇨🇳"),
        Language("Japanese", "ja", "🇯🇵"),
        Language("Korean", "ko", "🇰🇷"),
        Language("Turkish", "tr", "🇹🇷"),
        Language("Indonesian", "id", "🇮🇩"),
        Language("Thai", "th", "🇹🇭"),
        Language("Vietnamese", "vi", "🇻🇳"),
        Language("Italian", "it", "🇮🇹"),
        Language("Dutch", "nl", "🇳🇱"),
        Language("Swedish", "sv", "🇸🇪")
    )

    // Language tones
    val tones = listOf("Standard", "Casual", "Professional", "Friendly", "Formal", "Academic")

    // --- Single Translation States ---
    private val _singleInputText = MutableStateFlow("")
    val singleInputText = _singleInputText.asStateFlow()

    private val _singleSourceLang = MutableStateFlow(supportedLanguages[0]) // Auto-Detect
    val singleSourceLang = _singleSourceLang.asStateFlow()

    private val _singleTargetLang = MutableStateFlow(supportedLanguages[2]) // Spanish
    val singleTargetLang = _singleTargetLang.asStateFlow()

    private val _selectedTone = MutableStateFlow("Standard")
    val selectedTone = _selectedTone.asStateFlow()

    private val _correctGrammar = MutableStateFlow(false)
    val correctGrammar = _correctGrammar.asStateFlow()

    private val _singleResult = MutableStateFlow<TranslationResult?>(null)
    val singleResult = _singleResult.asStateFlow()

    private val _isTranslatingSingle = MutableStateFlow(false)
    val isTranslatingSingle = _isTranslatingSingle.asStateFlow()

    // --- Multi-Language Translation States ---
    private val _multiInputText = MutableStateFlow("")
    val multiInputText = _multiInputText.asStateFlow()

    private val _multiSourceLang = MutableStateFlow(supportedLanguages[1]) // English
    val multiSourceLang = _multiSourceLang.asStateFlow()

    // Selected multiple target languages (default: Spanish, French, German)
    private val _multiTargetLanguages = MutableStateFlow(listOf(supportedLanguages[2], supportedLanguages[3], supportedLanguages[4]))
    val multiTargetLanguages = _multiTargetLanguages.asStateFlow()

    private val _multiTone = MutableStateFlow("Standard")
    val multiTone = _multiTone.asStateFlow()

    private val _multiResult = MutableStateFlow<MultiTranslationResult?>(null)
    val multiResult = _multiResult.asStateFlow()

    private val _isTranslatingMulti = MutableStateFlow(false)
    val isTranslatingMulti = _isTranslatingMulti.asStateFlow()

    // --- Search & History States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Observe Room streams of historical records
    val historyList: StateFlow<List<TranslationRecord>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allHistory
            } else {
                repository.search(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<TranslationRecord>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme Mode Preference
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --- Action Methods ---

    fun updateSingleInput(text: String) {
        _singleInputText.value = text
    }

    fun setSingleSourceLang(lang: Language) {
        _singleSourceLang.value = lang
    }

    fun setSingleTargetLang(lang: Language) {
        if (lang.code != "auto") {
            _singleTargetLang.value = lang
        }
    }

    fun setSelectedTone(tone: String) {
        _selectedTone.value = tone
    }

    fun toggleGrammarCorrection() {
        _correctGrammar.value = !_correctGrammar.value
    }

    fun clearSingleInput() {
        _singleInputText.value = ""
        _singleResult.value = null
    }

    fun translateSingleText() {
        val text = _singleInputText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _isTranslatingSingle.value = true
            _singleResult.value = null

            val result = GeminiClient.translateSingle(
                text = text,
                sourceLang = _singleSourceLang.value.name,
                targetLang = _singleTargetLang.value.name,
                tone = _selectedTone.value,
                correctGrammar = _correctGrammar.value
            )

            _singleResult.value = result
            _isTranslatingSingle.value = false

            // Save success records to DB for persistence
            if (result is TranslationResult.Success) {
                repository.insert(
                    TranslationRecord(
                        originalText = text,
                        translatedText = result.translated,
                        sourceLang = if (_singleSourceLang.value.code == "auto") "Auto-Detect" else _singleSourceLang.value.name,
                        targetLang = _singleTargetLang.value.name,
                        tone = _selectedTone.value,
                        isFavorite = false
                    )
                )
            }
        }
    }

    // --- Multi-Translate Actions ---

    fun updateMultiInput(text: String) {
        _multiInputText.value = text
    }

    fun setMultiSourceLang(lang: Language) {
        _multiSourceLang.value = lang
    }

    fun setMultiTone(tone: String) {
        _multiTone.value = tone
    }

    fun toggleMultiTargetLanguage(lang: Language) {
        if (lang.code == "auto") return
        val currentList = _multiTargetLanguages.value.toMutableList()
        if (currentList.contains(lang)) {
            if (currentList.size > 1) { // Maintain at least one target
                currentList.remove(lang)
            }
        } else {
            currentList.add(lang)
        }
        _multiTargetLanguages.value = currentList
    }

    fun clearMultiInput() {
        _multiInputText.value = ""
        _multiResult.value = null
    }

    fun translateMultiText() {
        val text = _multiInputText.value.trim()
        val targets = _multiTargetLanguages.value.map { it.name }
        if (text.isEmpty() || targets.isEmpty()) return

        viewModelScope.launch {
            _isTranslatingMulti.value = true
            _multiResult.value = null

            val result = GeminiClient.translateMulti(
                text = text,
                sourceLang = _multiSourceLang.value.name,
                targetLanguages = targets,
                tone = _multiTone.value
            )

            _multiResult.value = result
            _isTranslatingMulti.value = false

            // Save individual language result entries to DB
            if (result is MultiTranslationResult.Success) {
                result.translations.forEach { (lang, translation) ->
                    // Just check that we got a valid translation
                    if (!translation.startsWith("Translation not returned", ignoreCase = true)) {
                        repository.insert(
                            TranslationRecord(
                                originalText = text,
                                translatedText = translation,
                                sourceLang = _multiSourceLang.value.name,
                                targetLang = lang,
                                tone = _multiTone.value,
                                isFavorite = false
                            )
                        )
                    }
                }
            }
        }
    }

    // --- History & Favorites Database Actions ---

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(record: TranslationRecord) {
        viewModelScope.launch {
            val updatedRecord = record.copy(isFavorite = !record.isFavorite)
            repository.update(updatedRecord)
        }
    }

    fun deleteHistoricalRecord(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}

class ViewModelFactory(
    private val application: Application,
    private val repository: TranslationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
            return TranslationViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
