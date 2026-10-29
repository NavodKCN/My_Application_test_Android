package com.example.myapplication_test.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication_test.data.model.Category
import com.example.myapplication_test.data.model.Priority
import com.example.myapplication_test.data.model.Task
import com.example.myapplication_test.data.repository.TaskRepository
import com.example.myapplication_test.ui.state.FilterMode
import com.example.myapplication_test.ui.state.TaskStats
import com.example.myapplication_test.ui.state.TaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private data class FilterOptions(
        val query: String,
        val mode: FilterMode,
        val priority: Priority?,
        val category: Category?
    )

    private val _searchQuery = MutableStateFlow("")
    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _isAddEditDialogVisible = MutableStateFlow(false)
    private val _taskToEdit = MutableStateFlow<Task?>(null)

    private val filterOptions = combine(
        _searchQuery,
        _filterMode,
        _selectedPriority,
        _selectedCategory
    ) { query, mode, priority, category ->
        FilterOptions(query, mode, priority, category)
    }

    val uiState: StateFlow<TaskUiState> = combine(
        repository.getTasks(),
        filterOptions,
        _isAddEditDialogVisible,
        _taskToEdit
    ) { allTasks, filters, isDialogVisible, taskToEdit ->

        val totalCount = allTasks.size
        val completedCount = allTasks.count { it.isCompleted }
        val pendingCount = totalCount - completedCount
        val stats = TaskStats(
            totalCount = totalCount,
            completedCount = completedCount,
            pendingCount = pendingCount
        )

        val filteredTasks = allTasks.filter { task ->
            val matchesSearch = filters.query.isBlank() ||
                    task.title.contains(filters.query, ignoreCase = true) ||
                    task.description.contains(filters.query, ignoreCase = true)

            val matchesMode = when (filters.mode) {
                FilterMode.ALL -> true
                FilterMode.PENDING -> !task.isCompleted
                FilterMode.COMPLETED -> task.isCompleted
            }

            val matchesPriority = filters.priority == null || task.priority == filters.priority
            val matchesCategory = filters.category == null || task.category == filters.category

            matchesSearch && matchesMode && matchesPriority && matchesCategory
        }

        TaskUiState(
            tasks = filteredTasks,
            searchQuery = filters.query,
            filterMode = filters.mode,
            selectedPriority = filters.priority,
            selectedCategory = filters.category,
            isAddEditDialogVisible = isDialogVisible,
            taskToEdit = taskToEdit,
            stats = stats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterModeChange(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun onPriorityFilterChange(priority: Priority?) {
        _selectedPriority.value = if (_selectedPriority.value == priority) null else priority
    }

    fun onCategoryFilterChange(category: Category?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onToggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }

    fun onDeleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun onClearCompleted() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    fun onOpenAddTaskDialog() {
        _taskToEdit.value = null
        _isAddEditDialogVisible.value = true
    }

    fun onOpenEditTaskDialog(task: Task) {
        _taskToEdit.value = task
        _isAddEditDialogVisible.value = true
    }

    fun onDismissDialog() {
        _isAddEditDialogVisible.value = false
        _taskToEdit.value = null
    }

    fun onSaveTask(
        title: String,
        description: String,
        priority: Priority,
        category: Category,
        dueDateFormatted: String
    ) {
        val currentTask = _taskToEdit.value
        viewModelScope.launch {
            if (currentTask != null) {
                val updatedTask = currentTask.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    dueDateFormatted = dueDateFormatted
                )
                repository.updateTask(updatedTask)
            } else {
                val newTask = Task(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    dueDateFormatted = dueDateFormatted
                )
                repository.addTask(newTask)
            }
            onDismissDialog()
        }
    }

    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(repository) as T
        }
    }
}
