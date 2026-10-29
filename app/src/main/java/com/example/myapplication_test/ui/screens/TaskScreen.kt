package com.example.myapplication_test.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication_test.ui.components.AddEditTaskDialog
import com.example.myapplication_test.ui.components.TaskCard
import com.example.myapplication_test.ui.components.TaskFilterSection
import com.example.myapplication_test.ui.components.TaskStatsCard
import com.example.myapplication_test.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TaskFlow",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Organize Your Day Efficiently",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (uiState.stats.completedCount > 0) {
                        TextButton(onClick = { viewModel.onClearCompleted() }) {
                            Text("Clear Done")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onOpenAddTaskDialog() },
                icon = { Text("➕", style = MaterialTheme.typography.titleMedium) },
                text = { Text("New Task", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Card
            item {
                TaskStatsCard(stats = uiState.stats)
            }

            // Search & Filter Section
            item {
                TaskFilterSection(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    filterMode = uiState.filterMode,
                    onFilterModeChange = viewModel::onFilterModeChange,
                    selectedPriority = uiState.selectedPriority,
                    onPriorityFilterChange = viewModel::onPriorityFilterChange,
                    selectedCategory = uiState.selectedCategory,
                    onCategoryFilterChange = viewModel::onCategoryFilterChange
                )
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasks (${uiState.tasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (uiState.selectedPriority != null || uiState.selectedCategory != null || uiState.searchQuery.isNotBlank()) {
                        TextButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                viewModel.onPriorityFilterChange(null)
                                viewModel.onCategoryFilterChange(null)
                            }
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }

            // Empty State
            if (uiState.tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("📝", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotBlank())
                                    "No tasks match '${uiState.searchQuery}'"
                                else
                                    "No tasks found for selected filters.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Task List Items
                items(
                    items = uiState.tasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onToggleCompletion = { viewModel.onToggleTaskCompletion(task.id) },
                        onEditClick = { viewModel.onOpenEditTaskDialog(task) },
                        onDeleteClick = { viewModel.onDeleteTask(task.id) }
                    )
                }
            }

            // Bottom Spacing for FloatingActionButton
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add / Edit Dialog
    if (uiState.isAddEditDialogVisible) {
        AddEditTaskDialog(
            taskToEdit = uiState.taskToEdit,
            onDismiss = viewModel::onDismissDialog,
            onSave = viewModel::onSaveTask
        )
    }
}
