package com.example.postmanager.model.client

import com.example.postmanager.model.response.WebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val webService : WebService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}