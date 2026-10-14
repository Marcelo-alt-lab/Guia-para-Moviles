package com.example.postmanager.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.navigation.NavHostController
import com.example.postmanager.viewmodel.PostViewModel

@Composable
fun EditPost(viewModel: PostViewModel, context: Context, nav: NavHostController){
    val post = viewModel.selectedPost

    var newTitle by remember { mutableStateOf(post.title) }
    var newBody by remember { mutableStateOf(post.body) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar Registro (ID: ${post.id})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = newTitle,
            onValueChange = { newTitle = it},
            label = { Text("Editar titulo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newBody,
            onValueChange = { newBody = it},
            label = { Text("Editar cuerpo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val updatedPost = post.copy(
                    title = newTitle,
                    body = newBody
                )
                viewModel.updatePost(context, updatedPost)
                Toast.makeText(context, "Registro Actualziado", Toast.LENGTH_SHORT).show()
                nav.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar")
        }

        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }


    }
}