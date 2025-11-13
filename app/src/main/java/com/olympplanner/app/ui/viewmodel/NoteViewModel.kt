package com.olympplanner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Note
import com.olympplanner.app.domain.model.NoteFilter
import com.olympplanner.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(NoteFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)

    val allNotes = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredNotes = combine(
        allNotes,
        selectedFilter,
        _selectedCategory,
        searchQuery
    ) { notes, filter, category, query ->
        var filtered = notes

        // Apply filter
        filtered = when (filter) {
            NoteFilter.ALL -> filtered
            NoteFilter.BY_CATEGORY -> if (category != null) {
                filtered.filter { it.category == category }
            } else filtered
            NoteFilter.PINNED -> filtered.filter { it.isPinned }
        }

        // Apply search
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }

        filtered
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setFilter(filter: NoteFilter) {
        _selectedFilter.value = filter
    }

    fun setSelectedCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun toggleNotePinned(noteId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            repository.updateNotePinned(noteId, isPinned)
        }
    }

    fun toggleNoteFavorite(noteId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateNoteFavorite(noteId, isFavorite)
        }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note.copy(id = 0))
        }
    }
}

