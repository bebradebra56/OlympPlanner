package com.olympplanner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.FilterType
import com.olympplanner.app.domain.model.Task
import com.olympplanner.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FilterType.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate = _selectedDate.asStateFlow()

    val allTasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredTasks = combine(allTasks, selectedFilter) { tasks, filter ->
        when (filter) {
            FilterType.ALL -> tasks
            FilterType.PERSONAL -> tasks.filter { it.category == Category.PERSONAL }
            FilterType.WORK -> tasks.filter { it.category == Category.WORK }
            FilterType.LEISURE -> tasks.filter { it.category == Category.LEISURE }
            FilterType.DEADLINES -> tasks.filter { !it.isCompleted && it.dateTime < System.currentTimeMillis() }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getTasksForWeek(weekStart: Long): Flow<Map<Long, List<Task>>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val weekEnd = calendar.timeInMillis + 7 * 24 * 60 * 60 * 1000L

        return repository.getTasksByDateRange(calendar.timeInMillis, weekEnd)
            .map { tasks ->
                tasks.groupBy { task ->
                    val taskCalendar = Calendar.getInstance()
                    taskCalendar.timeInMillis = task.dateTime
                    taskCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    taskCalendar.set(Calendar.MINUTE, 0)
                    taskCalendar.set(Calendar.SECOND, 0)
                    taskCalendar.set(Calendar.MILLISECOND, 0)
                    taskCalendar.timeInMillis
                }
            }
    }

    fun getTasksForDay(dayStart: Long): Flow<List<Task>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dayStart
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val end = calendar.timeInMillis

        return repository.getTasksForDay(start, end)
    }

    fun setFilter(filter: FilterType) {
        _selectedFilter.value = filter
    }

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun duplicateTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task.copy(id = 0))
        }
    }

    fun moveTaskToNextDay(task: Task) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.dateTime
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            repository.updateTask(task.copy(dateTime = calendar.timeInMillis))
        }
    }
}

