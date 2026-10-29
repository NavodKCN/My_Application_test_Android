package com.example.myapplication_test.viewmodel

import com.example.myapplication_test.data.model.Category
import com.example.myapplication_test.data.model.Priority
import com.example.myapplication_test.data.model.Task
import com.example.myapplication_test.data.repository.TaskRepository
import com.example.myapplication_test.ui.state.FilterMode
import com.example.myapplication_test.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
        viewModel = TaskViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initialStateReflectsRepositoryData() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(2, state.tasks.size)
        assertEquals(2, state.stats.totalCount)
        assertEquals(1, state.stats.completedCount)
        assertEquals(1, state.stats.pendingCount)
    }

    @Test
    fun searchFilter_filtersTasksByQuery() = runTest {
        viewModel.onSearchQueryChange("Workout")
        val state = viewModel.uiState.first()

        assertEquals(1, state.tasks.size)
        assertEquals("Workout", state.tasks.first().title)
    }

    @Test
    fun filterMode_pending_showsOnlyPendingTasks() = runTest {
        viewModel.onFilterModeChange(FilterMode.PENDING)
        val state = viewModel.uiState.first()

        assertEquals(1, state.tasks.size)
        assertFalse(state.tasks.first().isCompleted)
    }

    @Test
    fun openAddTaskDialog_updatesState() = runTest {
        viewModel.onOpenAddTaskDialog()
        val state = viewModel.uiState.first()

        assertTrue(state.isAddEditDialogVisible)
        assertNull(state.taskToEdit)
    }

    @Test
    fun saveNewTask_addsTaskAndClosesDialog() = runTest {
        viewModel.onOpenAddTaskDialog()
        viewModel.onSaveTask(
            title = "New Unit Test Task",
            description = "Description",
            priority = Priority.HIGH,
            category = Category.WORK,
            dueDateFormatted = "Tomorrow"
        )

        val state = viewModel.uiState.first()
        assertFalse(state.isAddEditDialogVisible)
        assertTrue(fakeRepository.getTasks().first().any { it.title == "New Unit Test Task" })
    }

    private class FakeTaskRepository : TaskRepository {
        private val tasks = MutableStateFlow(
            listOf(
                Task(id = "1", title = "Task 1", isCompleted = true, priority = Priority.HIGH, category = Category.WORK),
                Task(id = "2", title = "Workout", isCompleted = false, priority = Priority.MEDIUM, category = Category.HEALTH)
            )
        )

        override fun getTasks(): Flow<List<Task>> = tasks.asStateFlow()

        override suspend fun addTask(task: Task) {
            tasks.update { listOf(task) + it }
        }

        override suspend fun updateTask(task: Task) {
            tasks.update { current -> current.map { if (it.id == task.id) task else it } }
        }

        override suspend fun toggleTaskCompletion(id: String) {
            tasks.update { current ->
                current.map { if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it }
            }
        }

        override suspend fun deleteTask(id: String) {
            tasks.update { current -> current.filterNot { it.id == id } }
        }

        override suspend fun clearCompletedTasks() {
            tasks.update { current -> current.filterNot { it.isCompleted } }
        }
    }
}
