package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.data.model.PresentationData
import com.example.data.model.WorksheetData
import com.example.data.remote.GeminiService
import com.example.data.repository.DocRepository
import com.example.ui.components.NavDestination
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val geminiService = GeminiService()
    private val repository = DocRepository(db.docDao(), geminiService)

    val docs: StateFlow<List<GeneratedDoc>> = repository.allDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentDestination = MutableStateFlow(NavDestination.DASHBOARD)
    val currentDestination: StateFlow<NavDestination> = _currentDestination.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppTheme.BRIGHT_SAPPHIRE)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _selectedDocForPreview = MutableStateFlow<GeneratedDoc?>(null)
    val selectedDocForPreview: StateFlow<GeneratedDoc?> = _selectedDocForPreview.asStateFlow()

    private val _selectedWorksheetData = MutableStateFlow<WorksheetData?>(null)
    val selectedWorksheetData: StateFlow<WorksheetData?> = _selectedWorksheetData.asStateFlow()

    private val _selectedPresentationData = MutableStateFlow<PresentationData?>(null)
    val selectedPresentationData: StateFlow<PresentationData?> = _selectedPresentationData.asStateFlow()

    private val _lastGeneratedDoc = MutableStateFlow<GeneratedDoc?>(null)
    val lastGeneratedDoc: StateFlow<GeneratedDoc?> = _lastGeneratedDoc.asStateFlow()

    private val _lastWorksheetData = MutableStateFlow<WorksheetData?>(null)
    val lastWorksheetData: StateFlow<WorksheetData?> = _lastWorksheetData.asStateFlow()

    private val _lastPresentationData = MutableStateFlow<PresentationData?>(null)
    val lastPresentationData: StateFlow<PresentationData?> = _lastPresentationData.asStateFlow()

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _isTestingKey = MutableStateFlow(false)
    val isTestingKey: StateFlow<Boolean> = _isTestingKey.asStateFlow()

    private val _apiKeyTestResult = MutableStateFlow<String?>(null)
    val apiKeyTestResult: StateFlow<String?> = _apiKeyTestResult.asStateFlow()

    private val _isKeyValid = MutableStateFlow<Boolean?>(null)
    val isKeyValid: StateFlow<Boolean?> = _isKeyValid.asStateFlow()

    fun updateApiKey(newKey: String) {
        _apiKey.value = newKey
        geminiService.customApiKey = newKey
        _apiKeyTestResult.value = null
        _isKeyValid.value = null
    }

    fun testApiKey(keyToTest: String) {
        val key = keyToTest.trim()
        if (key.isBlank()) {
            _apiKeyTestResult.value = "Please enter an API Key to test."
            _isKeyValid.value = false
            return
        }

        viewModelScope.launch {
            _isTestingKey.value = true
            _apiKeyTestResult.value = "Testing key against Gemini API..."
            _isKeyValid.value = null

            val result = geminiService.testApiKey(key)
            result.onSuccess { msg ->
                _apiKeyTestResult.value = msg
                _isKeyValid.value = true
                _apiKey.value = key
                geminiService.customApiKey = key
            }.onFailure { err ->
                _apiKeyTestResult.value = err.message ?: "Key validation failed."
                _isKeyValid.value = false
            }

            _isTestingKey.value = false
        }
    }

    init {
        // Seed default sample documents if database is empty on first launch
        viewModelScope.launch {
            repository.docCount.collect { count ->
                if (count == 0) {
                    seedDefaultDocuments()
                }
            }
        }
    }

    fun navigateTo(destination: NavDestination) {
        _currentDestination.value = destination
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun selectTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }

    fun generateWorksheet(
        subject: String,
        gradeLevel: String,
        topic: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficulty: String
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val result = repository.generateAndSaveWorksheet(
                    subject, gradeLevel, topic, questionTypes, questionCount, difficulty
                )
                _lastGeneratedDoc.value = result.first
                _lastWorksheetData.value = result.second
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun generatePresentation(
        topic: String,
        targetAudience: String,
        slideCount: Int,
        designStyle: String,
        tone: String
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val result = repository.generateAndSavePresentation(
                    topic, targetAudience, slideCount, designStyle, tone
                )
                _lastGeneratedDoc.value = result.first
                _lastPresentationData.value = result.second
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun openDocPreview(doc: GeneratedDoc) {
        _selectedDocForPreview.value = doc
        if (doc.docType == DocType.WORKSHEET) {
            _selectedWorksheetData.value = repository.parseWorksheet(doc.contentJson)
            _selectedPresentationData.value = null
        } else {
            _selectedPresentationData.value = repository.parsePresentation(doc.contentJson)
            _selectedWorksheetData.value = null
        }
    }

    fun closeDocPreview() {
        _selectedDocForPreview.value = null
        _selectedWorksheetData.value = null
        _selectedPresentationData.value = null
    }

    fun toggleFavorite(doc: GeneratedDoc) {
        viewModelScope.launch {
            repository.toggleFavorite(doc.id, !doc.isFavorite)
        }
    }

    fun deleteDoc(doc: GeneratedDoc) {
        viewModelScope.launch {
            repository.deleteDoc(doc.id)
        }
    }

    private suspend fun seedDefaultDocuments() {
        try {
            repository.generateAndSaveWorksheet(
                subject = "Mathematics",
                gradeLevel = "Grade 8",
                topic = "Algebraic Linear Equations",
                questionTypes = listOf("Multiple Choice", "Short Answer"),
                questionCount = 8,
                difficulty = "Medium"
            )

            repository.generateAndSavePresentation(
                topic = "Modern AI Architecture",
                targetAudience = "Students & Developers",
                slideCount = 8,
                designStyle = "Minimal Sapphire",
                tone = "Educational"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
