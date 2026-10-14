package com.example.postmanager.model.response

import com.example.postmanager.model.beans.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WebService {

    @GET("posts/{id}")
    suspend fun getById(
        @Path("id") id: Int
    ) : Response<Post>
}