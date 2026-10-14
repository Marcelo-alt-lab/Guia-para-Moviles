package com.example.postmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.postmanager.ui.theme.PostManagerTheme
import com.example.postmanager.viewmodel.PostViewModel
import com.example.postmanager.views.nav.NavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel by viewModels<PostViewModel>()

        setContent {
            PostManagerTheme {
                NavHost(viewModel, this)
            }

        }
    }
}