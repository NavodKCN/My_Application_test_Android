package com.example.myapplication_test.data.repository

import com.example.myapplication_test.data.model.Category
import com.example.myapplication_test.data.model.Priority
import com.example.myapplication_test.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTaskRepository : TaskRepository {

    private val tasksState = MutableStateFlow(
        listOf(
            Task(
                title = "Explore Jetpack Compose UI",
                description = "Learn layout components, state management, and Material 3 design patterns.",
                isCompleted = true,
                priority = Priority.HIGH,
                category = Category.STUDY,
                dueDateFormatted = "Today"
            ),
            Task(
                title = "Design App Architecture",
                description = "Set up Repository pattern, ViewModel, and Unidirectional Data Flow.",
                isCompleted = true,
                priority = Priority.HIGH,
                category = Category.WORK,
                dueDateFormatted = "Today"
            ),
            Task(
                title = "Morning Workout",
                description = "30 minutes cardio and stretching session.",
                isCompleted = false,
                priority = Priority.MEDIUM,
                category = Category.HEALTH,
                dueDateFormatted = "Tomorrow"
            ),
            Task(
                title = "Review Weekly Budget",
                description = "Track expenses and plan savings for the coming month.",
                isCompleted = false,
                priority = Priority.LOW,
                category = Category.FINANCE,
                dueDateFormatted = "Fri, 10 PM"
            )
        )
    )

    override fun getTasks(): Flow<List<Task>> = tasksState.asStateFlow()

    override suspend fun addTask(task: Task) {
        tasksState.update { current ->
            listOf(task) + current
        }
    }

    override suspend fun updateTask(task: Task) {
        tasksState.update { current ->
            current.map { if (it.id == task.id) task else it }
        }
    }

    override suspend fun toggleTaskCompletion(id: String) {
        tasksState.update { current ->
            current.map {
                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
            }
        }
    }

    override suspend fun deleteTask(id: String) {
        tasksState.update { current ->
            current.filterNot { it.id == id }
        }
    }

    override suspend fun clearCompletedTasks() {
        tasksState.update { current ->
            current.filterNot { it.isCompleted }
        }
    }
}
