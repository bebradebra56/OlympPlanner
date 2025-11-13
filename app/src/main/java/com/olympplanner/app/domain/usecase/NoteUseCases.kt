package com.olympplanner.app.domain.usecase

import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Note
import com.olympplanner.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}

class GetNotesByCategoryUseCase(private val repository: NoteRepository) {
    operator fun invoke(category: Category): Flow<List<Note>> {
        return repository.getNotesByCategory(category)
    }
}

class GetPinnedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getPinnedNotes()
    }
}

class SearchNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return repository.searchNotes(query)
    }
}

class AddNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Long {
        return repository.insertNote(note)
    }
}

class UpdateNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note)
    }
}

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}

class ToggleNotePinnedUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Long, isPinned: Boolean) {
        repository.updateNotePinned(noteId, isPinned)
    }
}

