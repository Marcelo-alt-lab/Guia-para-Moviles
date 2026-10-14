package com.example.appbeauty.model.beans

data class Post(
    var id: String = "",
    var nombre: String = "",
    var marca: String = "",
    var precio: Double = 0.0,
    var categoria: String = "",
    var tipo: String = "",
    var descripcion: String = "",
    var foto: String = ""
)