package com.example.myapplication_test.ui.state

import com.example.myapplication_test.data.model.Category
import com.example.myapplication_test.data.model.Priority
import com.example.myapplication_test.data.model.Task

enum class FilterMode(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}

data class TaskStats(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val pendingCount: Int = 0
) {
    val completionRatio: Float
        get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val filterMode: FilterMode = FilterMode.ALL,
    val selectedPriority: Priority? = null,
    val selectedCategory: Category? = null,
    val isAddEditDialogVisible: Boolean = false,
    val taskToEdit: Task? = null,
    val stats: TaskStats = TaskStats()
)
