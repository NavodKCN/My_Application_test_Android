package com.example.myapplication_test.data.model

import java.util.UUID

enum class Priority(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class Category(val label: String) {
    WORK("Work"),
    PERSONAL("Personal"),
    HEALTH("Health"),
    FINANCE("Finance"),
    STUDY("Study")
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDateFormatted: String = ""
)
