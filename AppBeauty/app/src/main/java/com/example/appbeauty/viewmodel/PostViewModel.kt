package com.example.appbeauty.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appbeauty.model.beans.Post
import com.example.appbeauty.model.client.RetrofitClient
import com.example.appbeauty.model.db.OpenHelper
import kotlin.collections.arrayListOf

class PostViewModel : ViewModel(){
    var productList: List<Post> by mutableStateOf(listOf())

    suspend fun getPost(categoria: String) {
        val resource = RetrofitClient.webService.getPostCategories(categoria)
        if (resource.isSuccessful && resource.body() != null){
            productList = resource.body()!!
        }
    }

    // Requerimiento de igv y coste total/ modal del detalle
    var selectedPost: Post by mutableStateOf(Post())
    var showDialog: Boolean by mutableStateOf(false)

    var totalPrice: Double by mutableStateOf(0.0)
    var totalIgv: Double by mutableStateOf(0.0)

    var listaPost: MutableList<Post> by mutableStateOf(arrayListOf())

    // Guardar en la BD
    fun insertPost(context: Context, post: Post) {
        val dbHelper = OpenHelper(context)
        dbHelper.newPost(post)
        listarPost(context)
    }

    // Obtener de la DB
    fun listarPost(context: Context){
        val dbHelper = OpenHelper(context)
        listaPost = dbHelper.leerData()
        calcularTotales(context)
    }

    // Borrar de la DB
    fun eliminarPost(context: Context, id: String){
        val dbHelper = OpenHelper(context)
        dbHelper.eliminarPost(id)
        listarPost(context)
    }

    // Actualizar la DB
    fun actualizarPost(context: Context, post: Post){
        val dbHelper = OpenHelper(context)
        dbHelper.actualizarPost(post)
        listarPost(context)
    }

    // Guardar en Shared_Preferences
    private fun calcularTotales(context: Context){
        var suma = 0.0
        for (item in listaPost){
            suma += item.precio
        }
        val igv = suma * 0.18

        val prefs = context.getSharedPreferences("BeautyPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("total_price", suma.toFloat())
            putFloat("total_igv", igv.toFloat())
            apply()
        }

        totalPrice = prefs.getFloat("total_price", 0.0f).toDouble()
        totalIgv = prefs.getFloat("total_igv", 0.0f).toDouble()
    }
}



