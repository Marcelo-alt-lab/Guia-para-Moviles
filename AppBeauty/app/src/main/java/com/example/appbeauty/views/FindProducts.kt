package com.example.appbeauty.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appbeauty.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FindProducts(viewModel: PostViewModel, context: Context, nav: NavHostController){

    var categoriaBuscar by remember { mutableStateOf("") }

    val opciones = listOf("Maquillaje", "Facial", "Capilar")
    var seleccion by remember { mutableStateOf(opciones[0])}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp, horizontal = 15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = categoriaBuscar,
            label = { Text(text = "Categoria a encontrar") },
            onValueChange = { categoriaBuscar = it},
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO){
                    if (categoriaBuscar.isNotEmpty()){
                        viewModel.getPost(categoriaBuscar)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(text = "Consulta por categoria")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Categoria seleccionada: $seleccion")

        opciones.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (opcion == seleccion),
                        onClick = { seleccion = opcion }
                    )
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (opcion == seleccion),
                    onClick = { seleccion = opcion}
                )
                Text(
                    text = opcion,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO){
                    viewModel.getPost(seleccion)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {Text(text = "Consulta por categoria") }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.productList) { product ->
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
                        IconButton(
                            onClick = {
                                viewModel.selectedPost = product
                                viewModel.showDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Ver Detalle")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { nav.navigate("P3")},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir a Favoritos")
        }

    }

    // --- VENTANA MODAL (ALERT DIALOG) ---
    if (viewModel.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialog = false },
            title = {
                Text(text = viewModel.selectedPost.nombre, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "ID: ${viewModel.selectedPost.id}")
                    Text(text = "Marca: ${viewModel.selectedPost.marca}")
                    Text(text = "Categoría: ${viewModel.selectedPost.categoria}")
                    Text(text = "Tipo: ${viewModel.selectedPost.tipo}")
                    Text(text = "Precio: S/ ${viewModel.selectedPost.precio}")
                    Text(text = "Descripción: ${viewModel.selectedPost.descripcion}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Graba el producto completo en SQLite
                        viewModel.insertPost(context, viewModel.selectedPost)
                        viewModel.showDialog = false
                    }
                ) {
                    Text("Grabar Favorito")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}