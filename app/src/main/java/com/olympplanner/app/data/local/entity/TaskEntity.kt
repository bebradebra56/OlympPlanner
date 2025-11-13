package com.olympplanner.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Priority
import com.olympplanner.app.domain.model.RepeatType
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateTime: Long, // Timestamp in milliseconds
    val duration: Int = 0, // Duration in minutes
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatDays: List<Int> = emptyList(), // Days of week for custom repeat (1-7)
    val category: Category = Category.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val reminders: List<Long> = emptyList(), // Timestamps for reminders
    val checklist: List<ChecklistItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val colorHex: String? = null,
    val emoji: String? = null,
    val isAnchored: Boolean = false, // Fixed to this day
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ChecklistItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

class Converters {
    @TypeConverter
    fun fromRepeatType(value: RepeatType): String = value.name

    @TypeConverter
    fun toRepeatType(value: String): RepeatType = RepeatType.valueOf(value)

    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromLongList(value: List<Long>): String = value.joinToString(",")

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("||")

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        return value.split("||")
    }

    @TypeConverter
    fun fromChecklistItems(value: List<ChecklistItem>): String {
        return value.joinToString(";;") { "${it.id}||${it.text}||${it.isCompleted}" }
    }

    @TypeConverter
    fun toChecklistItems(value: String): List<ChecklistItem> {
        if (value.isEmpty()) return emptyList()
        return value.split(";;").map { item ->
            val parts = item.split("||")
            ChecklistItem(
                id = parts[0],
                text = parts[1],
                isCompleted = parts[2].toBoolean()
            )
        }
    }
}

