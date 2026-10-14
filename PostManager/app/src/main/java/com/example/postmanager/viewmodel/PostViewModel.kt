package com.example.postmanager.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.postmanager.model.beans.Post
import com.example.postmanager.model.client.RetrofitClient
import com.example.postmanager.model.db.OpenHelper

class PostViewModel : ViewModel(){
    var searchedPost: Post by mutableStateOf(Post())

    suspend fun fetchById(id: Int){
        val response = RetrofitClient.webService.getById(id)
        if (response.isSuccessful && response.body() != null){
            searchedPost = response.body()!!
        }
    }

    var selectedPost: Post by mutableStateOf(Post())
    var savedPostList: MutableList<Post> by mutableStateOf(mutableListOf())

    var totalCount: Int by mutableStateOf(0)

    fun insertPost(context: Context, post: Post){
        OpenHelper(context).newPost(post)
        loadPosts(context)
    }

    fun loadPosts(context: Context){
        savedPostList = OpenHelper(context).readAll()
        updateSharedPreferences(context)
    }

    fun deleteItem(context: Context, id: Int){
        OpenHelper(context).deletePost(id)
        loadPosts(context)
    }

    fun updatePost(context: Context, post: Post){
        OpenHelper(context).updateItem(post)
        loadPosts(context)
    }

    private fun updateSharedPreferences(context: Context){
        val count = savedPostList.size

        val prefs = context.getSharedPreferences("AppPrefsXML", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("total_count", count)
            apply()
        }

        totalCount = prefs.getInt("total_count", 0)
    }

}