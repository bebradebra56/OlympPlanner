package com.olympplanner.app.domain.usecase

import com.olympplanner.app.domain.model.Task
import com.olympplanner.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksForDayUseCase(private val repository: TaskRepository) {
    operator fun invoke(dayStart: Long, dayEnd: Long): Flow<List<Task>> {
        return repository.getTasksForDay(dayStart, dayEnd)
    }
}

class GetTasksForWeekUseCase(private val repository: TaskRepository) {
    operator fun invoke(weekStart: Long, weekEnd: Long): Flow<List<Task>> {
        return repository.getTasksByDateRange(weekStart, weekEnd)
    }
}

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long {
        return repository.insertTask(task)
    }
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
    }
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        repository.deleteTask(task)
    }
}

class ToggleTaskCompletionUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Long, isCompleted: Boolean) {
        repository.updateTaskCompletion(taskId, isCompleted)
    }
}

