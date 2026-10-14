# Guía Definitiva y Plantilla para el Examen Parcial de Desarrollo Móvil
> **Curso:** Aplicaciones para Dispositivos Móviles (Android con Kotlin y Jetpack Compose)  
> **Objetivo:** Plantilla modular paso a paso para resolver cualquier caso de examen (AppBeauty, TalentTrack, SongApp, etc.) obteniendo **20/20** según la rúbrica oficial.

---

## 1. Estructura de Paquetes (Arquitectura Limpia / MVVM)

Organizamos el código por responsabilidades dentro del paquete principal (`com.example.appname`):

```
com.example.appname
├── model/ 
│   ├── beans/       -> Data Classes Kotlin para modelar datos de API y BD
│   ├── client/      -> Singleton RetrofitClient con .baseUrl(...).addConverterFactory(...)
│   ├── db/          -> OpenHelper heredando de SQLiteOpenHelper (CRUD nativo completo)
│   └── response/    -> Interface WebService con endpoints @GET y suspend fun
├── viewmodel/       -> ViewModel con estados observables (mutableStateOf), corrutinas y SharedPreferences
└── views/ 
    ├── Home.kt              -> Pantalla inicial con Branding y ModalNavigationDrawer (Material 3)
    ├── FindProducts.kt      -> Búsqueda API (TextField / RadioButtons), LazyColumn y Modal AlertDialog
    ├── ProductsFavorite.kt  -> Lista local SQLite (Cards con botón editar precio y borrar) + Totales de SharedPreferences
    └── nav/ 
        └── NavPost.kt       -> NavHost y navegación entre pantallas (P1, P2, P3)
```

---

## 2. Permisos y Dependencias (`build.gradle.kts` y `AndroidManifest.xml`)

### A. Permiso de Internet en `app/src/main/AndroidManifest.xml`
Debe colocarse **antes** de la etiqueta `<application>`:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

### B. Dependencias en `app/build.gradle.kts`
Asegúrate de tener Retrofit, Gson, Navigation Compose, Coil (imágenes) y los iconos extendidos de Material:

```kotlin
dependencies {
    // Red: Retrofit 2 + Convertidor GSON
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Navegación entre pantallas en Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.9.8")

    // Carga de imágenes por URL (AsyncImage)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Iconos de Material Design (Search, Favorite, Menu, Edit, Delete, Info)
    implementation("androidx.compose.material:material-icons-extended")

    // Compose Material 3 base (ya viene por defecto)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    // ...
}
```
*(Sincroniza el proyecto con Gradle después de agregarlas).*

---

## 3. Modelo de Datos (`model/beans/Post.kt`)

Modelamos los atributos solicitados por el examen y la API:
```kotlin
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
```

---

## 4. Capa de Red (Retrofit y WebService)

### A. Interfaz `model/response/WebService.kt`
> **Regla de oro de Retrofit:**  
> - Si la URL es `...php?categoria=Maquillaje`, se usa `@Query("categoria")`.
> - Si la API retorna un corchete `[ { ... }, { ... } ]`, retorna `Response<List<Post>>`.
> - Si retorna un único objeto `{ ... }`, retorna `Response<Post>`.

```kotlin
package com.example.appbeauty.model.response

import com.example.appbeauty.model.beans.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("beauty_products.php")
    suspend fun getPostCategories(@Query("categoria") categoria: String): Response<List<Post>>
}
```

### B. Cliente Singleton `model/client/RetrofitClient.kt`
> **Regla Crítica:** La `baseUrl` **SIEMPRE debe terminar en barra `/`**.

```kotlin
package com.example.appbeauty.model.client

import com.example.appbeauty.model.response.WebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dev.codemcy.com/") // Obligatorio terminar con /
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}
```

---

## 5. Persistencia Local SQLite (`model/db/OpenHelper.kt`) - CRUD Completo

Implementamos las 4 operaciones (Create, Read, Update, Delete) de forma segura:
- `insertWithOnConflict(..., CONFLICT_REPLACE)` para evitar crasheos si el producto ya existe.
- Siempre cerrar la base de datos con `db.close()`.

```kotlin
package com.example.appbeauty.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appbeauty.model.beans.Post

class OpenHelper(context: Context) : SQLiteOpenHelper(
    context, "BeautyDB.db", null, 1
) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val query = "create table posts(id text primary key, nombre text, marca text, precio float, categoria text, tipo text, descripcion text, foto text)"
        p0!!.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val query = "drop table if exists posts"
        p0!!.execSQL(query)
        onCreate(p0)
    }

    // 1. CREATE: Guardar nuevo favorito (con reemplazo seguro)
    fun newPost(post: Post) {
        val db = this.writableDatabase
        val datos = ContentValues().apply {
            put("id", post.id)
            put("nombre", post.nombre)
            put("marca", post.marca)
            put("precio", post.precio)
            put("categoria", post.categoria)
            put("tipo", post.tipo)
            put("descripcion", post.descripcion)
            put("foto", post.foto)
        }
        db.insertWithOnConflict("posts", null, datos, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // 2. READ: Consultar todos los favoritos
    fun leerData(): MutableList<Post> {
        val db = this.readableDatabase
        val query = "select * from posts"
        val result = db.rawQuery(query, null)
        val listaP = mutableListOf<Post>()

        if (result.moveToFirst()) {
            do {
                listaP.add(
                    Post(
                        result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getDouble(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7)
                    )
                )
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listaP
    }

    // 3. DELETE: Eliminar por ID
    fun eliminarPost(id: String): Int {
        val db = this.writableDatabase
        val filasEliminadas = db.delete("posts", "id = ?", arrayOf(id))
        db.close()
        return filasEliminadas
    }

    // 4. UPDATE: Actualizar el precio de un registro
    fun actualizarPost(post: Post): Int {
        val db = this.writableDatabase
        val datos = ContentValues().apply {
            put("precio", post.precio)
        }
        val filasAfectadas = db.update("posts", datos, "id = ?", arrayOf(post.id))
        db.close()
        return filasAfectadas
    }
}
```

---

## 6. ViewModel con SharedPreferences (`viewmodel/PostViewModel.kt`)

Centraliza el estado observable, llamadas a Retrofit, CRUD de SQLite y cálculo en SharedPreferences.

> **Truco de Parcial:** Meter `calcularTotales(context)` **dentro de `listarPost`**. Así, cada vez que la app inicia o se inserta/edita/elimina un producto, la lista y los totales de SharedPreferences se sincronizan solos.

```kotlin
package com.example.appbeauty.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appbeauty.model.beans.Post
import com.example.appbeauty.model.client.RetrofitClient
import com.example.appbeauty.model.db.OpenHelper

class PostViewModel : ViewModel() {

    // --- A. Red (API) ---
    var productList: List<Post> by mutableStateOf(listOf())

    suspend fun getPost(categoria: String) {
        val resource = RetrofitClient.webService.getPostCategories(categoria)
        if (resource.isSuccessful && resource.body() != null) {
            productList = resource.body()!!
        }
    }

    // --- B. Control de Modal de Detalle ---
    var selectedPost: Post by mutableStateOf(Post())
    var showDialog: Boolean by mutableStateOf(false)

    // --- C. Persistencia Local (SQLite) ---
    var listaPost: MutableList<Post> by mutableStateOf(mutableListOf())

    // Variables observables para los totales (SharedPreferences)
    var totalPrice: Double by mutableStateOf(0.0)
    var totalIgv: Double by mutableStateOf(0.0)

    fun insertPost(context: Context, post: Post) {
        val dbHelper = OpenHelper(context)
        dbHelper.newPost(post)
        listarPost(context)
    }

    fun listarPost(context: Context) {
        val dbHelper = OpenHelper(context)
        listaPost = dbHelper.leerData()
        calcularTotales(context) // Sincroniza totales inmediatamente
    }

    fun eliminarPost(context: Context, id: String) {
        val dbHelper = OpenHelper(context)
        dbHelper.eliminarPost(id)
        listarPost(context)
    }

    fun actualizarPost(context: Context, post: Post) {
        val dbHelper = OpenHelper(context)
        dbHelper.actualizarPost(post)
        listarPost(context)
    }

    // --- D. SharedPreferences (Suma Total y 18% IGV) ---
    private fun calcularTotales(context: Context) {
        var suma = 0.0
        for (item in listaPost) {
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
```

---

## 7. Navegación y MainActivity

### `views/nav/NavPost.kt`
```kotlin
package com.example.appbeauty.views.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appbeauty.viewmodel.PostViewModel
import com.example.appbeauty.views.FindProducts
import com.example.appbeauty.views.Home
import com.example.appbeauty.views.ProductsFavorite

@Composable
fun NavPost(viewModel: PostViewModel, context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "P1") {
        composable("P1") { Home(viewModel, context, navController) }
        composable("P2") { FindProducts(viewModel, context, navController) }
        composable("P3") { ProductsFavorite(viewModel, context, navController) }
    }
}
```

### `MainActivity.kt`
```kotlin
package com.example.appbeauty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.appbeauty.ui.theme.AppBeautyTheme
import com.example.appbeauty.viewmodel.PostViewModel
import com.example.appbeauty.views.nav.NavPost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel by viewModels<PostViewModel>()

        setContent {
            AppBeautyTheme {
                NavPost(viewModel, this)
            }
        }
    }
}
```

---

## 8. Vistas con Jetpack Compose (Material 3)

### A. Vista Home (`views/Home.kt`) - Navigation Drawer y Branding
```kotlin
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
fun Home(viewModel: PostViewModel, context: Context, nav: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Menú Principal", modifier = Modifier.padding(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Find Products") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("P2")
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Products Favorite") },
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
                    title = { Text("AppBeauty") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
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
```

---

### B. Vista Búsqueda (`views/FindProducts.kt`) - TextField, RadioButtons, LazyColumn y Modal
```kotlin
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
fun FindProducts(viewModel: PostViewModel, context: Context, nav: NavHostController) {
    var searchCategory by remember { mutableStateOf("") }
    val options = listOf("Maquillaje", "Facial", "Capilar")
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 20.dp)
    ) {
        Text(text = "Buscar Productos", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Opción 1: Campo de texto libre
        OutlinedTextField(
            value = searchCategory,
            label = { Text("Escribe una categoría") },
            onValueChange = { searchCategory = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (searchCategory.isNotEmpty()) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        viewModel.getPost(searchCategory.trim())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("Buscar por texto")
        }

        // Opción 2: Filtro rápido con RadioButtons
        Text(text = "O selecciona una categoría:")
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option }
                    )
                    .padding(vertical = 2.dp)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { selectedOption = option }
                )
                Text(text = option, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Button(
            onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.getPost(selectedOption)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar por categoría seleccionada")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lista de resultados consumiendo viewModel.productList
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(viewModel.productList) { product ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.foto,
                            contentDescription = product.nombre,
                            modifier = Modifier.fillMaxWidth(0.35f).height(90.dp)
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Marca: ${product.marca}", fontSize = 13.sp)
                            Text(text = "Precio: S/ ${product.precio}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "ID: ${product.id}", fontSize = 11.sp)
                        }
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

        OutlinedButton(
            onClick = { nav.navigate("P3") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Favoritos")
        }
    }

    // Modal Flotante (AlertDialog)
    if (viewModel.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialog = false },
            title = { Text(text = viewModel.selectedPost.nombre, fontWeight = FontWeight.Bold) },
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
                        viewModel.insertPost(context, viewModel.selectedPost)
                        viewModel.showDialog = false
                    }
                ) {
                    Text("Grabar Favorito")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
```

---

### C. Vista Favoritos (`views/ProductsFavorite.kt`) - SQLite, 2 Íconos y Totales SharedPreferences
```kotlin
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun ProductsFavorite(viewModel: PostViewModel, context: Context, nav: NavHostController) {
    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf(Post()) }
    var newPriceText by remember { mutableStateOf("") }

    // Carga SQLite y SharedPreferences una sola vez al entrar
    LaunchedEffect(Unit) {
        viewModel.listarPost(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Lista de Favoritos",
            fontSize = 22.sp,
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
            Text("Volver a Buscar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lista de Favoritos desde SQLite (viewModel.listaPost)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(viewModel.listaPost) { product ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.foto,
                            contentDescription = product.nombre,
                            modifier = Modifier.width(90.dp).height(90.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Marca: ${product.marca}", fontSize = 13.sp)
                            Text(text = "Categoría: ${product.categoria} | ${product.tipo}", fontSize = 12.sp)
                            Text(text = "Precio: S/ ${"%.2f".format(product.precio)}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = product.descripcion, maxLines = 2, fontSize = 11.sp, color = Color.Gray)
                        }

                        // 2 Íconos requeridos: Editar precio y Eliminar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    productToEdit = product
                                    newPriceText = product.precio.toString()
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Precio", tint = Color(0xFF1976D2))
                            }

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

        Spacer(modifier = Modifier.height(10.dp))

        // Footer con totales calculados en SharedPreferences
        Text(
            text = "Suma Total de Precios: S/ ${"%.2f".format(viewModel.totalPrice)}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Total IGV (18%): S/ ${"%.2f".format(viewModel.totalIgv)}",
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }

    // Modal para editar precio
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Editar Precio de ${productToEdit.nombre}") },
            text = {
                Column {
                    Text(text = "Ingrese el nuevo precio:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPriceText,
                        onValueChange = { newPriceText = it },
                        label = { Text("Nuevo Precio (S/)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val precioActualizado = newPriceText.toDoubleOrNull() ?: productToEdit.precio
                        val productoFinal = productToEdit.copy(precio = precioActualizado)
                        viewModel.actualizarPost(context, productoFinal)
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar Precio")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
```

---

## 9. Checklist Definitivo para el 20/20

| # | Verificación | Criterio de Rúbrica | ¿Por qué importa? |
|---|---|---|---|
| 1 | **Permiso de Internet** | `C01` y `C03` | Sin `<uses-permission android:name="android.permission.INTERNET"/>` la API no responde. |
| 2 | **`baseUrl` con `/` final** | `C03` | Si no termina en `/`, Retrofit crashea en runtime. |
| 3 | **`@Query` vs `@Path`** | `C03` | En `file.php?cat=X`, usar `@Query("cat")`. En `posts/{id}`, usar `@Path("id")`. |
| 4 | **Navigation Drawer en Home** | `C02` | Rúbrica exige menú lateral deslizable (`ModalNavigationDrawer`) con branding. |
| 5 | **Modal de Detalle (`AlertDialog`)** | `C03` | Ícono que abre ventana flotante con campos completos y botón de grabar en SQLite. |
| 6 | **2 Íconos en Favoritos** | `C05` | Uno para borrar directo y otro para editar el precio abriendo un `AlertDialog`. |
| 7 | **SharedPreferences sincronizado** | `C04` y `C05` | Llamar a `calcularTotales(context)` dentro de `listarPost` para que siempre esté actualizado. |
| 8 | **Nomenclatura 100% en Inglés** | `C08` | Clases, variables y métodos en inglés (`searchCategory`, `newPriceText`, `productToEdit`). |
| 9 | **Ícono Personalizado** | `C09` | Cambiar el ícono por defecto en `res/mipmap` para no perder 1.0 punto. |
| 10 | **Export to Zip File** | **Penalidad (-2.0 pts)** | En Android Studio: **File $\rightarrow$ Export $\rightarrow$ Export to Zip File...** nombrado: `upc-pre-[periodo]-[curso]-ea-[codigo/apellidos].zip`. |
