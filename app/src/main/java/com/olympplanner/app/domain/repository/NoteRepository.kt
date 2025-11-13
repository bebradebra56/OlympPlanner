package com.olympplanner.app.domain.repository

import com.olympplanner.app.data.local.dao.NoteDao
import com.olympplanner.app.data.local.entity.NoteEntity
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)?.toDomain()
    }

    fun getNotesByCategory(category: Category): Flow<List<Note>> {
        return noteDao.getNotesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getPinnedNotes(): Flow<List<Note>> {
        return noteDao.getPinnedNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getFavoriteNotes(): Flow<List<Note>> {
        return noteDao.getFavoriteNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    suspend fun updateNotePinned(noteId: Long, isPinned: Boolean) {
        noteDao.updateNotePinned(noteId, isPinned)
    }

    suspend fun updateNoteFavorite(noteId: Long, isFavorite: Boolean) {
        noteDao.updateNoteFavorite(noteId, isFavorite)
    }

    private fun NoteEntity.toDomain() = Note(
        id = id,
        title = title,
        content = content,
        isChecklist = isChecklist,
        checklistItems = checklistItems.map { com.olympplanner.app.domain.model.ChecklistItem(it.id, it.text, it.isCompleted) },
        category = category,
        tags = tags,
        colorHex = colorHex,
        isPinned = isPinned,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        content = content,
        isChecklist = isChecklist,
        checklistItems = checklistItems.map { com.olympplanner.app.data.local.entity.ChecklistItem(it.id, it.text, it.isCompleted) },
        category = category,
        tags = tags,
        colorHex = colorHex,
        isPinned = isPinned,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

