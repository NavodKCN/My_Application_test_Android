package com.example.myapplication_test.repository

import com.example.myapplication_test.data.model.Category
import com.example.myapplication_test.data.model.Priority
import com.example.myapplication_test.data.model.Task
import com.example.myapplication_test.data.repository.InMemoryTaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryTaskRepositoryTest {

    private lateinit var repository: InMemoryTaskRepository

    @Before
    fun setUp() {
        repository = InMemoryTaskRepository()
    }

    @Test
    fun getTasks_returnsInitialTasks() = runTest {
        val tasks = repository.getTasks().first()
        assertTrue(tasks.isNotEmpty())
    }

    @Test
    fun addTask_addsTaskToHeadOfList() = runTest {
        val newTask = Task(
            title = "Test Task",
            description = "Test Description",
            priority = Priority.HIGH,
            category = Category.WORK
        )

        repository.addTask(newTask)
        val tasks = repository.getTasks().first()

        assertEquals(newTask.id, tasks.first().id)
        assertEquals("Test Task", tasks.first().title)
    }

    @Test
    fun toggleTaskCompletion_togglesState() = runTest {
        val initialTasks = repository.getTasks().first()
        val targetTask = initialTasks.first()
        val initialStatus = targetTask.isCompleted

        repository.toggleTaskCompletion(targetTask.id)
        val updatedTasks = repository.getTasks().first()
        val updatedTask = updatedTasks.first { it.id == targetTask.id }

        assertEquals(!initialStatus, updatedTask.isCompleted)
    }

    @Test
    fun deleteTask_removesTaskFromList() = runTest {
        val initialTasks = repository.getTasks().first()
        val targetTask = initialTasks.first()

        repository.deleteTask(targetTask.id)
        val updatedTasks = repository.getTasks().first()

        assertFalse(updatedTasks.any { it.id == targetTask.id })
    }

    @Test
    fun clearCompletedTasks_removesAllCompletedTasks() = runTest {
        repository.clearCompletedTasks()
        val remainingTasks = repository.getTasks().first()

        assertTrue(remainingTasks.none { it.isCompleted })
    }
}
