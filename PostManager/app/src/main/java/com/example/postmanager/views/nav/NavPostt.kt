package com.example.postmanager.views.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.postmanager.viewmodel.PostViewModel
import com.example.postmanager.views.EditPost
import com.example.postmanager.views.FindPost
import com.example.postmanager.views.ListOfPost

@Composable
fun NavHost(viewModel: PostViewModel, context: Context){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "FindPost"){
        composable("EditPost") { EditPost(viewModel, context, navController) }
        composable("FindPost") { FindPost(viewModel, context, navController) }
        composable("ListOfPost") { ListOfPost(viewModel, context, navController) }
    }
}