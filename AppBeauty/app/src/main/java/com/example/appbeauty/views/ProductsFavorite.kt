package com.example.appbeauty.views

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appbeauty.model.beans.Post
import com.example.appbeauty.viewmodel.PostViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductsFavorite(viewModel: PostViewModel, context: Context, nav: NavHostController){

    var listaDPost: MutableList<Post> by mutableStateOf(arrayListOf())

    var showEditDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf(Post()) }
    var nuevoPrecioTexto by remember { mutableStateOf("") }

    viewModel.listarPost(context)
    listaDPost = viewModel.listaPost

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(vertical = 30.dp)
    ) {
        Text(
            text = "Lista de Favoritos",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
            )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { nav.navigate("P2") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Encontrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.listaPost) { product ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Foto del producto
                        AsyncImage(
                            model = product.foto,
                            contentDescription = product.nombre,
                            modifier = Modifier
                                .fillMaxWidth(0.35f)
                                .height(100.dp)
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        // Datos resumidos (Requisito 2: id, nombre, marca, precio)
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = product.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Marca: ${product.marca}", fontSize = 14.sp)
                            Text(text = "Precio: S/ ${product.precio}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = "ID: ${product.id}", fontSize = 12.sp)
                        }
                        // Ícono que abre el Detalle en Ventana Modal (Requisito 3)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Ícono 1: EDITAR PRECIO
                            IconButton(
                                onClick = {
                                    productoAEditar = product
                                    nuevoPrecioTexto = product.precio.toString()
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Precio", tint = Color(0xFF1976D2))
                            }
                            // Ícono 2: ELIMINAR FAVORITO
                            IconButton(
                                onClick = {
                                    viewModel.eliminarPost(context, product.id)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Favorito", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Precio Total: ${"%.2f".format(viewModel.totalPrice)}",
            modifier = Modifier.padding(vertical = 3.dp)
        )

        Text(
            text = "Igv Total: ${"%.2f".format(viewModel.totalIgv)}",
            color = Color.Red
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(text = "Editar Precio de ${productoAEditar.nombre}")
            },
            text = {
                Column {
                    Text(text = "Ingrese el nuevo precio para este producto:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevoPrecioTexto,
                        onValueChange = { nuevoPrecioTexto = it },
                        label = { Text("Nuevo Precio (S/)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val precioActualizado = nuevoPrecioTexto.toDoubleOrNull() ?: productoAEditar.precio
                        // Creamos una copia del producto con el nuevo precio y actualizamos en SQLite
                        val productoFinal = productoAEditar.copy(precio = precioActualizado)
                        viewModel.actualizarPost(context, productoFinal)
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar Precio")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}