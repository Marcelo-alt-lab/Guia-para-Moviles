package com.example.appbeauty.model.response

import com.example.appbeauty.model.beans.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("beauty_products.php")
    suspend fun getPostCategories(@Query("categoria") categoria: String): Response<List<Post>>
}