package com.example.postmanager.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.postmanager.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FindPost(viewModel: PostViewModel, context: Context, nav: NavHostController){
    var inputQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .padding(top = 24.dp)
    ) {

        Text(text = "Consulta API (Retrofit)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // --- SECCIÓN 1: BÚSQUEDA POR TEXTO / ID (Como el "1" de la pizarra) ---
        OutlinedTextField(
            value = inputQuery,
            onValueChange = { inputQuery = it },
            label = { Text("Ingrese ID o Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (inputQuery.isNotEmpty()) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        viewModel.fetchById(inputQuery.trim().toInt())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONSULTAR")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Text(text = "id: ${viewModel.searchedPost.id}")
            Text(text = "title: ${viewModel.searchedPost.title}")
            Text(text = "body: ${viewModel.searchedPost.body}")
        }

        Button(
            onClick = {
                viewModel.insertPost(context, viewModel.searchedPost)
                Toast.makeText(context, "Guardado en SQLite", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("GUARDAR")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { nav.navigate("ListOfPost") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Lista SQLite (Favoritos)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                viewModel.logoutUser(context)
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                nav.navigate("LoginPost") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            val user = viewModel.getLoggedUser(context)
            Text(if (user.isNotEmpty()) "Cerrar Sesión ($user)" else "Cerrar Sesión")
        }

    }


}