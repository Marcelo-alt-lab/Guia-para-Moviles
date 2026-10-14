package com.example.postmanager.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.postmanager.viewmodel.PostViewModel

@Composable
fun ListOfPost(viewModel: PostViewModel, context: Context, nav: NavHostController){

    LaunchedEffect(Unit) {
        viewModel.loadPosts(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp)
    ) {

        Text(
            text = "Registros Guardados (SQLite)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(viewModel.savedPostList){ post ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = post.id.toString())
                            Text(text = post.title)
                            Text(text = post.body)
                        }

                        // Botón (X) -> Eliminar de SQLite
                        IconButton(
                            onClick = { viewModel.deleteItem(context, post.id) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar")
                        }

                        // Botón [E] -> Ir a la Pantalla 3 de Edición
                        IconButton(
                            onClick = {
                                viewModel.selectedPost = post
                                nav.navigate("EditPost")
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Eliminar")
                        }
                    }
                }

            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total de Posts: ${viewModel.totalCount}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

    }

}