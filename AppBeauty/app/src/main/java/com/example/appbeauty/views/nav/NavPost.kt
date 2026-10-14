package com.example.appbeauty.views.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appbeauty.viewmodel.PostViewModel
import com.example.appbeauty.views.FindProducts
import com.example.appbeauty.views.Home
import com.example.appbeauty.views.ProductsFavorite

@Composable
fun NavPost(viewModel: PostViewModel, context: Context){
    val recordarPantalla = rememberNavController()

    NavHost(navController = recordarPantalla, startDestination = "P1") {
        composable("P1") { Home(viewModel, context, recordarPantalla)  }
        composable("P2") { FindProducts(viewModel, context, recordarPantalla) }
        composable("P3") { ProductsFavorite(viewModel, context, recordarPantalla)}
    }
}