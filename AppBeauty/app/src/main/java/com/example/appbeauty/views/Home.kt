package com.example.appbeauty.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appbeauty.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: PostViewModel, context: Context, nav: NavHostController){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Menu Principal",
                    modifier = Modifier.padding(15.dp)
                )

                // P2
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null)},
                        label = { Text("FindProducts")},
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate("P2")
                        }

                )

                // P3
                NavigationDrawerItem(
                    icon = {Icon(Icons.Default.Favorite, contentDescription = null)},
                    label = {Text("Products Favorite")},
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("P3")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text("AppBeauty")},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "https://tse3.mm.bing.net/th/id/OIP.pC6AlfRiI0SwCNKffcNFJAHaEb?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
                    contentDescription = "Branding AppBeauty",
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
        }
    }
}