package com.example.myapplication_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication_test.data.repository.InMemoryTaskRepository
import com.example.myapplication_test.ui.screens.TaskScreen
import com.example.myapplication_test.ui.theme.MyApplicationtestTheme
import com.example.myapplication_test.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationtestTheme {
                val repository = remember { InMemoryTaskRepository() }
                val taskViewModel: TaskViewModel = viewModel(
                    factory = TaskViewModel.Factory(repository)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskScreen(viewModel = taskViewModel)
                }
            }
        }
    }
}
