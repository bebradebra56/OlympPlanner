package com.olympplanner.app.domain.repository

import com.olympplanner.app.data.local.dao.TaskDao
import com.olympplanner.app.data.local.entity.TaskEntity
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    fun getTasksByDateRange(startTime: Long, endTime: Long): Flow<List<Task>> {
        return taskDao.getTasksByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTasksByDateRangeAndCategory(
        startTime: Long,
        endTime: Long,
        category: Category
    ): Flow<List<Task>> {
        return taskDao.getTasksByDateRangeAndCategory(startTime, endTime, category)
            .map { entities -> entities.map { it.toDomain() } }
    }

    fun getOverdueTasks(currentTime: Long): Flow<List<Task>> {
        return taskDao.getOverdueTasks(currentTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTasksByCategory(category: Category): Flow<List<Task>> {
        return taskDao.getTasksByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }

    fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> {
        return taskDao.getTasksForDay(dayStart, dayEnd).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun TaskEntity.toDomain() = Task(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        duration = duration,
        repeatType = repeatType,
        repeatDays = repeatDays,
        category = category,
        priority = priority,
        isCompleted = isCompleted,
        reminders = reminders,
        checklist = checklist.map { com.olympplanner.app.domain.model.ChecklistItem(it.id, it.text, it.isCompleted) },
        tags = tags,
        colorHex = colorHex,
        emoji = emoji,
        isAnchored = isAnchored,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Task.toEntity() = TaskEntity(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        duration = duration,
        repeatType = repeatType,
        repeatDays = repeatDays,
        category = category,
        priority = priority,
        isCompleted = isCompleted,
        reminders = reminders,
        checklist = checklist.map { com.olympplanner.app.data.local.entity.ChecklistItem(it.id, it.text, it.isCompleted) },
        tags = tags,
        colorHex = colorHex,
        emoji = emoji,
        isAnchored = isAnchored,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

