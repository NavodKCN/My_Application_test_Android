package com.example.myapplication_test.data.repository

import com.example.myapplication_test.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun toggleTaskCompletion(id: String)
    suspend fun deleteTask(id: String)
    suspend fun clearCompletedTasks()
}
