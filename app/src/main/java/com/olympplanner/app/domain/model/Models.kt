package com.olympplanner.app.domain.model

enum class Category {
    WORK, PERSONAL, LEISURE
}

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class RepeatType {
    NONE, DAILY, WEEKLY, WEEKDAYS, CUSTOM
}

enum class TimeSegment {
    MORNING, AFTERNOON, EVENING
}

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateTime: Long,
    val duration: Int = 0,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatDays: List<Int> = emptyList(),
    val category: Category = Category.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val reminders: List<Long> = emptyList(),
    val checklist: List<ChecklistItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val colorHex: String? = null,
    val emoji: String? = null,
    val isAnchored: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ChecklistItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val isChecklist: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val category: Category? = null,
    val tags: List<String> = emptyList(),
    val colorHex: String? = null,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class FilterType {
    ALL, PERSONAL, WORK, LEISURE, DEADLINES
}

enum class NoteFilter {
    ALL, BY_CATEGORY, PINNED
}

