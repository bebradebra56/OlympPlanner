package com.olympplanner.app.data.local.dao

import androidx.room.*
import com.olympplanner.app.data.local.entity.TaskEntity
import com.olympplanner.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dateTime ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE dateTime >= :startTime AND dateTime < :endTime ORDER BY dateTime ASC")
    fun getTasksByDateRange(startTime: Long, endTime: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dateTime >= :startTime AND dateTime < :endTime AND category = :category ORDER BY dateTime ASC")
    fun getTasksByDateRangeAndCategory(
        startTime: Long,
        endTime: Long,
        category: Category
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dateTime < :currentTime ORDER BY dateTime ASC")
    fun getOverdueTasks(currentTime: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dateTime ASC")
    fun getTasksByCategory(category: Category): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)

    @Query("SELECT * FROM tasks WHERE dateTime >= :dayStart AND dateTime < :dayEnd ORDER BY dateTime ASC")
    fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>
}

