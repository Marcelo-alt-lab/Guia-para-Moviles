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
import com.example.postmanager.views.LoginPost

@Composable
fun AppNavigation(viewModel: PostViewModel, context: Context){
    val navController = rememberNavController()
    val startRoute = if (viewModel.isUserLoggedIn(context)) "FindPost" else "LoginPost"

    NavHost(navController = navController, startDestination = startRoute){
        composable("LoginPost") { LoginPost(viewModel, context, navController) }
        composable("EditPost") { EditPost(viewModel, context, navController) }
        composable("FindPost") { FindPost(viewModel, context, navController) }
        composable("ListOfPost") { ListOfPost(viewModel, context, navController) }
    }
}