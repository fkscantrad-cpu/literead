package com.literead.viewmodel

import androidx.lifecycle.*
import com.literead.data.model.Bookmark
import com.literead.data.model.Document
import com.literead.data.model.UserPreferences
import com.literead.data.repository.DocumentRepository
import com.literead.utils.PreferencesManager
import kotlinx.coroutines.launch

/**
 * ViewModel pour le lecteur (Reader)
 */
class ReaderViewModel(
    private val repository: DocumentRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _currentDocument = MutableLiveData<Document>()
    val currentDocument: LiveData<Document> = _currentDocument

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _bookmarks = MutableLiveData<List<Bookmark>>()
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks

    private val _preferences = MutableLiveData<UserPreferences>()
    val preferences: LiveData<UserPreferences> = _preferences

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _preferences.value = preferencesManager.getPreferences()
    }

    fun loadDocument(documentId: Long) {
        viewModelScope.launch {
            try {
                val document = repository.getDocumentById(documentId)
                if (document != null) {
                    _currentDocument.value = document
                    _currentPage.value = document.lastReadPage
                    _totalPages.value = document.totalPages

                    // Charger les signets
                    repository.getBookmarks(documentId).observeForever { bookmarks ->
                        _bookmarks.value = bookmarks
                    }
                } else {
                    _errorMessage.value = "Document not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error loading document"
            }
        }
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
        _currentDocument.value?.let { document ->
            viewModelScope.launch {
                repository.updateReadingProgress(document.id, page)
            }
        }
    }

    fun toggleBookmark(page: Int) {
        _currentDocument.value?.let { document ->
            viewModelScope.launch {
                val hasBookmark = repository.hasBookmark(document.id, page)
                if (hasBookmark) {
                    repository.removeBookmark(document.id, page)
                } else {
                    repository.addBookmark(document.id, page)
                }
            }
        }
    }

    fun updatePreferences(preferences: UserPreferences) {
        _preferences.value = preferences
        preferencesManager.savePreferences(preferences)
    }

    fun setTheme(theme: String) {
        preferencesManager.setTheme(theme)
        val current = _preferences.value ?: UserPreferences()
        _preferences.value = current.copy(theme = theme)
    }

    fun setReadingMode(mode: String) {
        preferencesManager.setReadingMode(mode)
        val current = _preferences.value ?: UserPreferences()
        _preferences.value = current.copy(readingMode = mode)
    }

    fun setFontSize(size: Int) {
        preferencesManager.setFontSize(size)
        val current = _preferences.value ?: UserPreferences()
        _preferences.value = current.copy(fontSize = size)
    }

    fun setBrightness(brightness: Float) {
        preferencesManager.setBrightness(brightness)
        val current = _preferences.value ?: UserPreferences()
        _preferences.value = current.copy(brightness = brightness)
    }
}
