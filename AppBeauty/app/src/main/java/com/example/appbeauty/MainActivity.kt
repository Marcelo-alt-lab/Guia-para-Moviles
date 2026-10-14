package com.example.appbeauty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.appbeauty.ui.theme.AppBeautyTheme
import com.example.appbeauty.viewmodel.PostViewModel
import com.example.appbeauty.views.nav.NavPost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel by viewModels<PostViewModel>()

        setContent {
            AppBeautyTheme {
                NavPost(viewModel, this)
            }
        }
    }
}
