# Guía Maestra y Plantilla Genérica para Examen de Desarrollo Móvil

> **Arquitectura:** MVVM (Kotlin + Jetpack Compose + Retrofit + SQLite + SharedPreferences)  
> **Instrucción rápida:** Usa `Ctrl + F` y reemplaza `com.example.appname` por el paquete de tu proyecto, y adapta los campos de `ItemBean` según el JSON del examen.

---

## Índice de Contenidos

1. [1. Estructura de Paquetes (Arquitectura MVVM)](#1-estructura-de-paquetes-arquitectura-mvvm)
2. [2. Permisos y Dependencias (AndroidManifest.xml y build.gradle.kts)](#2-permisos-y-dependencias-androidmanifestxml-y-buildgradlekts)
   - [A. Permiso de Internet en app/src/main/AndroidManifest.xml](#a-permiso-de-internet-en-appsrcmainandroidmanifestxml)
   - [B. Dependencias en app/build.gradle.kts (Module :app)](#b-dependencias-en-appbuildgradlekts-module-app)
3. [3. Modelo de Datos Genérico (model/beans/ItemBean.kt)](#3-modelo-de-datos-genérico-modelbeansitembeankt)
4. [4. Capa de Red: Casos de Consulta (model/response/WebService.kt y RetrofitClient.kt)](#4-capa-de-red-casos-de-consulta-modelresponsewebservicekt-y-retrofitclientkt)
   - [A. Interfaz WebService.kt (Elige el caso que venga en el examen)](#a-interfaz-webservicekt-elige-el-caso-que-venga-en-el-examen)
   - [B. Cliente Singleton model/client/RetrofitClient.kt](#b-cliente-singleton-modelclientretrofitclientkt)
5. [5. Base de Datos Local SQLite (model/db/OpenHelper.kt)](#5-base-de-datos-local-sqlite-modeldbopenhelperkt)
6. [6. ViewModel Centralizado (viewmodel/MainViewModel.kt)](#6-viewmodel-centralizado-viewmodelmainviewmodelkt)
7. [7. Bloques Modulares de UI (Copia y Pega según pida el examen)](#7-bloques-modulares-de-ui-copia-y-pega-según-pida-el-examen)
   - [A. Implementar un Selector (RadioButton) para Consulta](#a-implementar-un-selector-radiobutton-para-consulta)
8. [8. Las 3 Pantallas Exactas de la Pizarra (Más Home opcional)](#8-las-3-pantallas-exactas-de-la-pizarra-más-home-opcional)
   - [Pantalla 1 (Izquierda de la Pizarra): views/SearchView.kt](#pantalla-1-izquierda-de-la-pizarra-viewssearchviewkt)
   - [Pantalla 2 (Centro de la Pizarra): views/FavoriteListView.kt](#pantalla-2-centro-de-la-pizarra-viewsfavoritelistviewkt)
   - [Pantalla 3 (Derecha de la Pizarra): views/EditItemView.kt](#pantalla-3-derecha-de-la-pizarra-viewsedititemviewkt)
   - [Pantalla Opcional: views/HomeView.kt (Si te piden Menú Lateral / Drawer)](#pantalla-opcional-viewshomeviewkt-si-te-piden-menú-lateral--drawer)
   - [Pantalla Opcional: views/LoginView.kt (Si te piden Inicio de Sesión con SharedPreferences)](#pantalla-opcional-viewsloginviewkt-si-te-piden-inicio-de-sesión-con-sharedpreferences)
9. [9. Navegación y MainActivity.kt](#9-navegación-y-mainactivitykt)
   - [A. views/nav/AppNavigation.kt](#a-viewsnavappnavigationkt)
   - [B. MainActivity.kt](#b-mainactivitykt)
10. [10. Resumen Rápido de Cambios el Día del Examen](#10-resumen-rápido-de-cambios-el-día-del-examen)
11. [11. Guía de Solución de Errores y Trampas de Examen (Troubleshooting)](#11-guía-de-solución-de-errores-y-trampas-de-examen-troubleshooting)
12. [12. Segmento Especial: JSONs Complejos (Wrappers {"data": [...]} y Objetos Anidados)](#12-segmento-especial-jsons-complejos-wrappers-data--y-objetos-anidados)
   - [A. Caso 1: Objeto Envoltorio / Wrapper ({ "data": [ ... ] })](#a-caso-1-objeto-envoltorio--wrapper--data---)
   - [B. Caso 2: Objetos Anidados en el mismo registro (Ej: street: { ... })](#b-caso-2-objetos-anidados-en-el-mismo-registro-ej-street---)
   - [C. Tabla Comparativa Mnemotécnica](#c-tabla-comparativa-mnemotécnica)

---

## 1. Estructura de Paquetes (Arquitectura MVVM)

Crea estos paquetes dentro de tu paquete principal (`com.example.appname`):

```text
com.example.appname
├── model/ 
│   ├── beans/       -> ItemBean.kt (Data Class que mapea el JSON y la tabla SQLite)
│   ├── client/      -> RetrofitClient.kt (Singleton con baseUrl)
│   ├── db/          -> OpenHelper.kt (SQLiteOpenHelper con CRUD completo)
│   └── response/    -> WebService.kt (Interface con variantes @GET, @Query y @Path)
├── viewmodel/       -> MainViewModel.kt (Estados, Corrutinas, SQLite y SharedPreferences)
└── views/ 
    ├── HomeView.kt          -> (Opcional) Menú Lateral (Drawer) + Branding
    ├── SearchView.kt        -> (Pantalla 1 Pizarra) Consulta API + Selector + Guardar en SQLite
    ├── FavoriteListView.kt  -> (Pantalla 2 Pizarra) Lista SQLite + Eliminar (X) + Ir a Editar (E) + SharedPreferences
    ├── EditItemView.kt      -> (Pantalla 3 Pizarra) Formulario con 3+ cajas de texto + Botón "EDITAR"
    ├── LoginView.kt         -> (Opcional) Pantalla de Login con persistencia en SharedPreferences
    └── nav/ 
        └── AppNavigation.kt -> NavHost (Rutas: "login", "home", "search", "list", "edit")
```

---

## 2. Permisos y Dependencias (AndroidManifest.xml y build.gradle.kts)

### A. Permiso de Internet en app/src/main/AndroidManifest.xml
Colocar antes de la etiqueta `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

### B. Dependencias en app/build.gradle.kts (Module :app)

```kotlin
dependencies {
    // 1. Retrofit 2 + GSON
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // 2. Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // 3. Coil (Imágenes desde URL con AsyncImage)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 4. Iconos Extendidos de Material
    implementation("androidx.compose.material:material-icons-extended")
}
```

---

## 3. Modelo de Datos Genérico (model/beans/ItemBean.kt)

> **Tip:** Si el JSON de la API usa un nombre distinto al de tu variable, usa `@SerializedName("nombre_en_json")`.

```kotlin
package com.example.appname.model.beans

data class ItemBean(
    ("id") var id: String = "",
    ("nombre") var title: String = "",        // Ej: nombre, titulo, modelo
    ("marca") var subtitle: String = "",      // Ej: marca, autor, artista
    ("categoria") var category: String = "",  // Ej: categoria, genero, tipo
    ("precio") var value: Double = 0.0,       // Ej: precio, salario, puntaje
    ("descripcion") var description: String = "",
    ("foto") var imageUrl: String = ""
)
```

---

## 4. Capa de Red: Casos de Consulta (model/response/WebService.kt y RetrofitClient.kt)

### A. Interfaz WebService.kt (Elige el caso que venga en el examen)

```kotlin
package com.example.appname.model.response

import com.example.appname.model.beans.ItemBean
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    // =========================================================================
    // CASO 1: Consulta por Categoría / Filtro con @Query -> URL: archivo.php?categoria=Valor
    // =========================================================================
    @GET("endpoint.php")
    suspend fun getByCategoryQuery(
        @Query("categoria") category: String
    ): Response<List<ItemBean>>

    // =========================================================================
    // CASO 2: Consulta por Atributo / ID con @Path (Devuelve 1 solo Objeto) -> URL: posts/1
    // (Exactamente como la Pantalla 1 de la pizarra donde pones "1" y das a CONSULTAR)
    // =========================================================================
    @GET("posts/{id}")
    suspend fun getByIdPath(
        @Path("id") id: String
    ): Response<ItemBean>

    // =========================================================================
    // CASO 3: Consulta por Atributo / Categoría con @Path (Devuelve Lista) -> URL: items/categoria/{cat}
    // =========================================================================
    @GET("items/categoria/{cat}")
    suspend fun getListByPath(
        @Path("cat") category: String
    ): Response<List<ItemBean>>

    // =========================================================================
    // CASO 4: Consulta General sin parámetros (@GET simple) -> URL: items
    // =========================================================================
    @GET("items")
    suspend fun getAllItems(): Response<List<ItemBean>>
}
```

### B. Cliente Singleton model/client/RetrofitClient.kt

> **Regla Crítica:** La baseUrl SIEMPRE debe terminar con `/`.

```kotlin
package com.example.appname.model.client

import com.example.appname.model.response.WebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/" // <-- CAMBIAR URL AQUÍ (Terminar en /)

    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}
```

---

## 5. Base de Datos Local SQLite (model/db/OpenHelper.kt)

Incluye el CRUD completo: Insertar (`newItem`), Listar (`readAll`), Eliminar (`deleteItem`) y Editar (`updateItem` actualizando múltiples campos como en la Pantalla 3 de la pizarra).

*Nota: El "items" mencionado en la pizarra es la tabla a crear, no el nombre de la base de datos*

```kotlin
package com.example.appname.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appname.model.beans.ItemBean

class OpenHelper(context: Context) : SQLiteOpenHelper(
    context, "AppDatabase.db", null, 1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE items(
                id TEXT PRIMARY KEY,
                title TEXT,
                subtitle TEXT,
                category TEXT,
                value REAL,
                description TEXT,
                imageUrl TEXT
            )
        """.trimIndent()
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS items")
        onCreate(db)
    }

    // 1. GUARDAR (CREATE) - Evita crasheo si el ID se repite
    // item is the Entity/Model to be saved so the name can be whatever
    fun newItem(item: ItemBean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", item.id)
            put("title", item.title)
            put("subtitle", item.subtitle)
            put("category", item.category)
            put("value", item.value)
            put("description", item.description)
            put("imageUrl", item.imageUrl)
        }
        db.insertWithOnConflict("items", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // 2. LISTAR (READ)
    fun readAll(): MutableList<ItemBean> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM items", null)
        val list = mutableListOf<ItemBean>()

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    ItemBean(
                        id = cursor.getString(0),
                        title = cursor.getString(1),
                        subtitle = cursor.getString(2),
                        category = cursor.getString(3),
                        value = cursor.getDouble(4),
                        description = cursor.getString(5),
                        imageUrl = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // 3. ELIMINAR (DELETE - Botón "X" de la pizarra)
    fun deleteItem(id: String): Int {
        val db = this.writableDatabase
        val rows = db.delete("items", "id = ?", arrayOf(id))
        db.close()
        return rows
    }

    // 4. EDITAR (UPDATE - Botón "EDITAR" de la Pantalla 3 de la pizarra)
    fun updateItem(item: ItemBean): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", item.title)
            put("subtitle", item.subtitle)
            put("category", item.category)
            put("value", item.value)
            put("description", item.description)
        }
        val rows = db.update("items", values, "id = ?", arrayOf(item.id))
        db.close()
        return rows
    }
}
```

---

## 6. ViewModel Centralizado (viewmodel/MainViewModel.kt)

Maneja los métodos de consulta (`@Query` y `@Path`), el CRUD de SQLite y el cálculo automático en SharedPreferences (tanto el TOTAL: 4 de registros como la suma de precios e IGV).

```kotlin
package com.example.appname.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appname.model.beans.ItemBean
import com.example.appname.model.client.RetrofitClient
import com.example.appname.model.db.OpenHelper

class MainViewModel : ViewModel() {

    // --- A. ESTADOS DE RED (API) ---
    var apiItemList: List<ItemBean> by mutableStateOf(listOf()) // Obligatorio, el nombre de la variable es irrelevante. Lo que importa es el tipo de dato
    var singleSearchedItem: ItemBean by mutableStateOf(ItemBean()) // Para cuando busca 1 solo item por ID (@Path)

    // Opción 1: Buscar lista por Categoría (@Query)
    suspend fun fetchByCategoryQuery(category: String) {
        try {
            val response = RetrofitClient.webService.getByCategoryQuery(category)
            if (response.isSuccessful && response.body() != null) {
                apiItemList = response.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Opción 2: Buscar 1 objeto por Atributo/ID (@Path) -> Como en la Pizarra
    suspend fun fetchByIdPath(id: String) {
        try {
            val response = RetrofitClient.webService.getByIdPath(id)
            if (response.isSuccessful && response.body() != null) {
                singleSearchedItem = response.body()!!
                apiItemList = listOf(singleSearchedItem) // También lo mete a la lista por si usas LazyColumn
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Opción 3: Buscar lista por Atributo (@Path)
    suspend fun fetchListByPath(category: String) {
        try {
            val response = RetrofitClient.webService.getListByPath(category)
            if (response.isSuccessful && response.body() != null) {
                apiItemList = response.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- B. CONTROL DE SELECCIÓN Y EDICIÓN ---
    var selectedItem: ItemBean by mutableStateOf(ItemBean())
    var showDetailDialog: Boolean by mutableStateOf(false)

    // --- C. ESTADOS DE BASE DE DATOS LOCAL (SQLite) ---
    var savedItemList: MutableList<ItemBean> by mutableStateOf(mutableListOf())

    // --- D. ESTADOS DE SHARED PREFERENCES (XML) ---
    var totalCount: Int by mutableStateOf(0)      // Para "TOTAL: 4" de la pizarra
    var totalAmount: Double by mutableStateOf(0.0) // Por si pide suma de precios/montos
    var totalTax: Double by mutableStateOf(0.0)    // Por si pide 18% IGV

    fun insertItem(context: Context, item: ItemBean) {
        OpenHelper(context).newItem(item)
        loadSavedItems(context)
    }

    fun loadSavedItems(context: Context) {
        savedItemList = OpenHelper(context).readAll()
        updateSharedPreferences(context) // Sincroniza SharedPreferences automáticamente
    }

    fun deleteItem(context: Context, id: String) {
        OpenHelper(context).deleteItem(id)
        loadSavedItems(context)
    }

    fun updateItem(context: Context, item: ItemBean) {
        OpenHelper(context).updateItem(item)
        loadSavedItems(context)
    }

    // --- E. LÓGICA DE SHARED PREFERENCES (XML) ---
    private fun updateSharedPreferences(context: Context) {
        val count = savedItemList.size // total de registros en la base de datos
        val sum = savedItemList.sumOf { it.value } // suma de los precios
        val igv = sum * 0.18 // cálculo del IGV

        val prefs = context.getSharedPreferences("AppPrefsXML", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("total_count", count)
            putFloat("total_amount", sum.toFloat())
            putFloat("total_tax", igv.toFloat())
            apply()
        }

        // Leemos desde el XML de SharedPreferences hacia las variables de la UI
        totalCount = prefs.getInt("total_count", 0)
        totalAmount = prefs.getFloat("total_amount", 0.0f).toDouble()
        totalTax = prefs.getFloat("total_tax", 0.0f).toDouble()
    }

    // --- F. (OPCIONAL) GESTIÓN DE SESIÓN CON SHARED PREFERENCES ---
    var loggedUser: String by mutableStateOf("")

    // Verifica si ya hay una sesión activa al abrir la app
    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLogged = prefs.getBoolean("is_logged", false)
        if (isLogged) {
            loggedUser = prefs.getString("username", "") ?: ""
        }
        return isLogged
    }

    // Guarda la sesión en SharedPreferences
    fun loginUser(context: Context, user: String, pass: String): Boolean {
        if (user.isNotBlank() && pass.isNotBlank()) {
            val prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean("is_logged", true)
                putString("username", user)
                apply()
            }
            loggedUser = user
            return true
        }
        return false
    }

    // Cierra y borra la sesión de SharedPreferences
    fun logoutUser(context: Context) {
        val prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        prefs.edit().apply {
            clear()
            apply()
        }
        loggedUser = ""
    }
}
```

---

## 7. Bloques Modulares de UI (Copia y Pega según pida el examen)

### A. Implementar un Selector (RadioButton) para Consulta
Úsalo dentro de `SearchView.kt` si te piden elegir entre opciones predefinidas (ej. Categorías, Tipos, Géneros):

```kotlin
// 1. Variables de estado para el RadioButton (colocar arriba en el @Composable)
val radioOptions = listOf("Opción 1", "Opción 2", "Opción 3") // <-- Cambiar por las categorías del examen
var selectedOption by remember { mutableStateOf(radioOptions[0]) }

// 2. Diseño de los RadioButtons + Botón de Consulta
Text(text = "Selecciona una categoría:", fontWeight = FontWeight.Bold)
radioOptions.forEach { option ->
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
            // Usa fetchByCategoryQuery(selectedOption) si es @Query
            // O usa fetchListByPath(selectedOption) si es @Path
            viewModel.fetchByCategoryQuery(selectedOption)
        }
    },
    modifier = Modifier.fillMaxWidth()
) {
    Text("CONSULTAR POR SELECTOR")
}
```

---

## 8. Las 3 Pantallas Exactas de la Pizarra (Más Home opcional)

### Pantalla 1 (Izquierda de la Pizarra): views/SearchView.kt
Tiene la caja de texto para buscar (ej. "1" o una categoría), el botón CONSULTAR, muestra los datos obtenidos de Retrofit y tiene el botón GUARDAR hacia SQLite.

```kotlin
package com.example.appname.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appname.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchView(viewModel: MainViewModel, context: Context, nav: NavHostController) {
    var inputQuery by remember { mutableStateOf("") }

    // Selector RadioButton por si el examen pide filtro por opciones
    val radioOptions = listOf("CategoriaA", "CategoriaB", "CategoriaC")
    var selectedRadio by remember { mutableStateOf(radioOptions[0]) }

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
                        // CAMBIAR SEGÚN EXAMEN:
                        // Si busca por ID (@Path): viewModel.fetchByIdPath(inputQuery.trim())
                        // Si busca por Categoría (@Query): viewModel.fetchByCategoryQuery(inputQuery.trim())
                        viewModel.fetchByIdPath(inputQuery.trim())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONSULTAR")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SECCIÓN 2: SELECTOR RADIO BUTTON (Descomentar o usar si el examen lo pide) ---
        Text(text = "Filtro por Selector:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            radioOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = (option == selectedRadio),
                        onClick = { selectedRadio = option }
                    )
                ) {
                    RadioButton(
                        selected = (option == selectedRadio),
                        onClick = { selectedRadio = option }
                    )
                    Text(text = option, fontSize = 13.sp)
                }
            }
        }
        OutlinedButton(
            onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.fetchByCategoryQuery(selectedRadio)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Consultar Categoría Seleccionada")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SECCIÓN 3: RESULTADOS Y BOTÓN GUARDAR EN SQLITE ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(viewModel.apiItemList) { item ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "ID: ${item.id}", fontWeight = FontWeight.Bold)
                                Text(text = "Título: ${item.title}", fontSize = 15.sp)
                                Text(text = "Subtítulo: ${item.subtitle}", fontSize = 13.sp)
                                Text(text = "Valor: S/ ${item.value}", fontWeight = FontWeight.SemiBold)
                            }
                            // Botón para abrir Modal (si el examen pide guardar desde Modal)
                            IconButton(onClick = {
                                viewModel.selectedItem = item
                                viewModel.showDetailDialog = true
                            }) {
                                Icon(Icons.Default.Info, contentDescription = "Detalle")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón GUARDAR directo (Tal cual el dibujo de la pizarra)
                        Button(
                            onClick = {
                                viewModel.insertItem(context, item)
                                Toast.makeText(context, "Guardado en SQLite", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("GUARDAR")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { nav.navigate("list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Lista SQLite (Favoritos)")
        }
    }

    // Modal de Detalle (Por si la rúbrica pide grabar desde ventana flotante)
    if (viewModel.showDetailDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDetailDialog = false },
            title = { Text(text = viewModel.selectedItem.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ID: ${viewModel.selectedItem.id}")
                    Text("Subtítulo: ${viewModel.selectedItem.subtitle}")
                    Text("Categoría: ${viewModel.selectedItem.category}")
                    Text("Valor: S/ ${viewModel.selectedItem.value}")
                    Text("Descripción: ${viewModel.selectedItem.description}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.insertItem(context, viewModel.selectedItem)
                    viewModel.showDetailDialog = false
                }) {
                    Text("GUARDAR")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.showDetailDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
```

### Pantalla 2 (Centro de la Pizarra): views/FavoriteListView.kt
Muestra las Cards leídas desde SQLite, cada una con su botón Eliminar (X) y su botón Editar (E) que navega a la Pantalla 3, más la barra inferior con el TOTAL guardado en SharedPreferences (XML).

```kotlin
package com.example.appname.views

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appname.viewmodel.MainViewModel

@Composable
fun FavoriteListView(viewModel: MainViewModel, context: Context, nav: NavHostController) {

    // Carga los datos de SQLite y actualiza SharedPreferences al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadSavedItems(context)
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

        OutlinedButton(
            onClick = { nav.navigate("search") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("<- Volver a Consultar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lista de Cards como en el centro de la pizarra
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(viewModel.savedItemList) { item ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                modifier = Modifier.size(70.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        // Las 3 líneas de texto dentro de la Card (según pizarra)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = item.subtitle, fontSize = 13.sp)
                            Text(text = "S/ ${"%.2f".format(item.value)} | ${item.category}", fontSize = 13.sp)
                        }

                        // Botón (X) -> Eliminar de SQLite
                        IconButton(
                            onClick = { viewModel.deleteItem(context, item.id) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar (X)", tint = Color.Red)
                        }

                        // Botón [E] -> Ir a la Pantalla 3 de Edición
                        IconButton(
                            onClick = {
                                viewModel.selectedItem = item
                                nav.navigate("edit")
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar (E)", tint = Color(0xFF1976D2))
                        }
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Footer de SharedPreferences (XML) tal como en la pizarra ("TOTAL: 4" + Montos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TOTAL REGISTROS: ${viewModel.totalCount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Suma Total: S/ ${"%.2f".format(viewModel.totalAmount)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "IGV (18%): S/ ${"%.2f".format(viewModel.totalTax)}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
```

### Pantalla 3 (Derecha de la Pizarra): views/EditItemView.kt
Recibe el ítem cuando presionas el botón [E], carga sus datos en las 3 cajas de texto (puedes agregar más si deseas) y al presionar el botón EDITAR actualiza SQLite y regresa a la lista.

```kotlin
package com.example.appname.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appname.viewmodel.MainViewModel

@Composable
fun EditItemView(viewModel: MainViewModel, context: Context, nav: NavHostController) {
    val item = viewModel.selectedItem

    // Las 3 cajas de texto dibujadas en la pizarra
    var titleText by remember { mutableStateOf(item.title) }
    var subtitleText by remember { mutableStateOf(item.subtitle) }
    var valueText by remember { mutableStateOf(item.value.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar Registro (ID: ${item.id})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Caja de texto 1
        OutlinedTextField(
            value = titleText,
            onValueChange = { titleText = it },
            label = { Text("Campo 1 (Título / Nombre)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Caja de texto 2
        OutlinedTextField(
            value = subtitleText,
            onValueChange = { subtitleText = it },
            label = { Text("Campo 2 (Subtítulo / Marca)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Caja de texto 3
        OutlinedTextField(
            value = valueText,
            onValueChange = { valueText = it },
            label = { Text("Campo 3 (Precio / Valor)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón [EDITAR] de la pizarra
        Button(
            onClick = {
                val updatedItem = item.copy(
                    title = titleText,
                    subtitle = subtitleText,
                    value = valueText.toDoubleOrNull() ?: item.value
                )
                viewModel.updateItem(context, updatedItem)
                Toast.makeText(context, "Registro actualizado", Toast.LENGTH_SHORT).show()
                nav.popBackStack() // Regresa a la Pantalla 2 (Lista)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("EDITAR")
        }

        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
```

### Pantalla Opcional: views/HomeView.kt (Si te piden Menú Lateral / Drawer)

```kotlin
package com.example.appname.views

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appname.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MainViewModel, context: Context, nav: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Menú de Opciones", modifier = Modifier.padding(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Consultar API") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("search")
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Lista SQLite") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("list")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App Examen") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
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
                    model = "https://picsum.photos/600/400",
                    contentDescription = "Banner",
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
            }
        }
    }
}
```

### Pantalla Opcional: views/LoginView.kt (Si te piden Inicio de Sesión con SharedPreferences)

Permite autenticar un usuario, guardar su estado de sesión en `SharedPreferences` para que no tenga que volver a iniciar sesión si cierra y vuelve a abrir la app, y redirigir a la pantalla principal limpiando el stack (`popUpTo`) para que el botón "Atrás" de Android no lo devuelva al login.

```kotlin
package com.example.appname.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appname.viewmodel.MainViewModel

@Composable
fun LoginView(viewModel: MainViewModel, context: Context, nav: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario o Correo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    // Guarda la sesión en SharedPreferences (XML)
                    viewModel.loginUser(context, username)
                    Toast.makeText(context, "Bienvenido, $username", Toast.LENGTH_SHORT).show()

                    // Navega a la pantalla de búsqueda y saca 'login' de la pila
                    nav.navigate("search") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("INGRESAR")
        }
    }
}
```

#### Botón de Cerrar Sesión (Snippet para colocar en HomeView o Drawer)
Si el profesor pide opción de "Cerrar Sesión" (Logout):
```kotlin
Button(
    onClick = {
        viewModel.logoutUser(context)
        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        nav.navigate("login") {
            popUpTo(0) { inclusive = true } // Limpia toda la pila de navegación
        }
    },
    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
    modifier = Modifier.fillMaxWidth()
) {
    Text("Cerrar Sesión")
}
```

---

## 9. Navegación y MainActivity.kt

### A. views/nav/AppNavigation.kt

> **Tip:**
> - Si piden **Inicio de Sesión**: usa `startDestination = if (viewModel.isUserLoggedIn(context)) "search" else "login"`.
> - Si no piden Login y arranca directo en la consulta (como en la pizarra): usa `startDestination = "search"`.

```kotlin
package com.example.appname.views.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appname.viewmodel.MainViewModel
import com.example.appname.views.EditItemView
import com.example.appname.views.FavoriteListView
import com.example.appname.views.HomeView
import com.example.appname.views.LoginView
import com.example.appname.views.SearchView

@Composable
fun AppNavigation(viewModel: MainViewModel, context: Context) {
    val navController = rememberNavController()

    // Si tu examen pide Login con persistencia en SharedPreferences:
    // val startRoute = if (viewModel.isUserLoggedIn(context)) "search" else "login"
    val startRoute = "search" // o "login" si se evalúa sesión

    NavHost(navController = navController, startDestination = startRoute) {
        composable("login") { LoginView(viewModel, context, navController) }
        composable("home") { HomeView(viewModel, context, navController) }
        composable("search") { SearchView(viewModel, context, navController) }
        composable("list") { FavoriteListView(viewModel, context, navController) }
        composable("edit") { EditItemView(viewModel, context, navController) }
    }
}
```

### B. MainActivity.kt

```kotlin
package com.example.appname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.appname.ui.theme.AppNameTheme // <-- Ajustar al nombre de tu Theme
import com.example.appname.viewmodel.MainViewModel
import com.example.appname.views.nav.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel by viewModels<MainViewModel>()

        setContent {
            AppNameTheme {
                AppNavigation(viewModel, this)
            }
        }
    }
}
```

---

## 10. Resumen Rápido de Cambios el Día del Examen

- **En RetrofitClient.kt:** Pega la URL base del examen asegurándote de que termine en `/`.
- **En WebService.kt:**
  - Si pide buscar por URL tipo `archivo.php?tipo=X` $\rightarrow$ Usa `@Query("tipo")`.
  - Si pide buscar por URL tipo `posts/{id}` $\rightarrow$ Usa `@Path("id")`.
- **Exportar sin perder puntos:** Ve a `File -> Export -> Export to Zip File...`.

---

## 11. Guía de Solución de Errores y Trampas de Examen (Troubleshooting)

### Error 1: `Cause: jlink executable ... redhat.java ... does not exist`
- **¿Por qué pasa?** Gradle intenta autodetectar un JDK mediante toolchains y selecciona un **JRE** instalado por una extensión externa (como Red Hat de VS Code), el cual carece de la herramienta `jlink.exe`.
- **Solución 1 (Desde Android Studio):**
  1. Ve a: **File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle**.
  2. Despliega **`> Advanced Settings`** (debajo de *Version: 25* o similar).
  3. En el desplegable **Vendor**, cambia de `Any vendor` a **`JetBrains`** (que es el JBR oficial integrado de Android Studio).
  4. Clic en **Apply -> OK** y sincroniza con Gradle.
- **Solución 2 (Directa en archivo `gradle.properties`):**
  Agrega al final de `gradle.properties`:
  ```properties
  org.gradle.java.home=C:\\Users\\<TuUsuario>\\AppData\\Local\\Programs\\Android Studio\\jbr
  ```

---

### Error 2: `SQLiteException: no such table: PostDB` (Crasheo fatal en runtime)
- **¿Por qué pasa?** Confundir el nombre de la base de datos (archivo `.db`) con el nombre de la tabla. Creas la tabla como `CREATE TABLE posts(...)`, pero en `insert`, `rawQuery`, `delete` o `update` escribes `"PostDB"`.
- **Regla:**
  - En el constructor: `SQLiteOpenHelper(context, "PostDB.db", null, 1)`.
  - En las consultas SQL: la tabla debe llamarse exactamente igual en todos lados (`"posts"` o `"items"`).
  - Usa siempre `db.insertWithOnConflict("posts", null, values, SQLiteDatabase.CONFLICT_REPLACE)` para evitar crasheos si se intenta registrar dos veces el mismo ID.
  - Cierra siempre `db.close()` y `cursor.close()`.

---

### Error 3: Conflicto de Scroll (`IllegalStateException: Vertically scrollable component...`)
- **¿Por qué pasa?** Ponerle `.verticalScroll(rememberScrollState())` al `Column` principal y adentro meter un `LazyColumn(Modifier.weight(1f))`. En Compose dos contenedores verticales con scroll no pueden anidarse directamente.
- **Regla:** Quita `.verticalScroll(...)` del `Column` contenedor. El `LazyColumn` ya se encarga automáticamente del scroll de toda la lista.

---

### Error 4: El Footer del Total (TOTAL: X) flota encima de la pantalla
- **¿Por qué pasa?** En la pantalla de lista (Pantalla 2), cerrar el `Column` antes de la tarjeta del total (`Card` con `TOTAL: ${viewModel.totalCount}`). Si el `Card` queda fuera del `Column`, Compose lo dibuja flotando sobre el contenido superior.
- **Regla:** Coloca el `Card` del total **DENTRO** del `Column(Modifier.fillMaxSize())`, posicionado inmediatamente después del `LazyColumn(Modifier.weight(1f))`.

---

### Error 5: Crasheo al presionar "CONSULTAR" sin escribir nada (`NumberFormatException`)
- **¿Por qué pasa?** Hacer `input.toInt()` cuando el usuario no ha escrito nada o puso letras.
- **Regla:** Usa siempre parseo seguro con `toIntOrNull()`:
  ```kotlin
  inputQuery.trim().toIntOrNull()?.let { id ->
      viewModel.fetchById(id)
  } ?: Toast.makeText(context, "Ingrese un ID válido", Toast.LENGTH_SHORT).show()
  ```

---

### Error 6: Sombreado de Nombres en Navegación (Name Shadowing)
- **¿Por qué pasa?** Nombrar tu función `@Composable fun NavHost(...)` con el mismo nombre que la función nativa `NavHost(...)` de Navigation Compose.
- **Regla:** Nombra a tu función de rutas como `AppNavigation(...)` o `NavPost(...)`.

---

### Error 7: Recomposición Infinita al abrir la Lista SQLite
- **¿Por qué pasa?** Llamar a `viewModel.loadPosts(context)` suelto en el cuerpo de la función `@Composable`. Cada vez que Compose dibuja la pantalla, vuelve a leer el disco y se genera un bucle de recomposición.
- **Regla:** Envuélvelo siempre dentro de `LaunchedEffect(Unit)`:
  ```kotlin
  LaunchedEffect(Unit) {
      viewModel.loadPosts(context)
  }
  ```


---

## 12. Segmento Especial: JSONs Complejos (Wrappers {"data": [...]} y Objetos Anidados)

> 🧠 **Regla de Oro:**
> - Cada par de llaves `{ }` en el JSON representa una **`data class` independiente** en Kotlin.
> - Cada par de corchetes `[ ]` representa una lista: **`List<TuClase>`**.

---

### A. Caso 1: Objeto Envoltorio / Wrapper ({ "data": [ ... ] })

Ocurre cuando la API no devuelve una lista directa `[ ... ]`, sino un objeto raíz que contiene una propiedad (comúnmente `"data"`, `"results"`, `"items"` o `"songs"`) con el array de elementos adentro:

```json
{
  "data": [
    {
      "id": "1",
      "name": "Espresso",
      "artists": "Sabrina Carpenter",
      "isExplicit": true,
      "durationMs": 175459,
      "albumName": "Espresso",
      "albumReleaseDate": "2024-04-12"
    }
  ]
}
```

> ⚠️ **Peligro en el examen:** Si declaras `Response<List<SongBean>>` en tu WebService, la aplicación crasheará en runtime con:  
> `JsonSyntaxException: Expected BEGIN_ARRAY but was BEGIN_OBJECT`.

#### Paso 1: En `model/beans/SongBean.kt` (Crear el Bean individual Y el Envoltorio)

```kotlin
package com.example.appname.model.beans

// 1. Representa cada registro individual dentro del array
data class SongBean(
    val id: String = "",
    val name: String = "",
    val artists: String = "",
    val isExplicit: Boolean = false,
    val durationMs: Long = 0L,
    val albumName: String = "",
    val albumReleaseDate: String = ""
)

// 2. Representa el objeto raíz exterior. El nombre del atributo DEBE coincidir con la clave del JSON ("data")
data class SongResponse(
    val data: List<SongBean> = emptyList()
)
```

#### Paso 2: En `model/response/WebService.kt` (Pedir el Envoltorio)

```kotlin
package com.example.appname.model.response

import com.example.appname.model.beans.SongResponse
import retrofit2.Response
import retrofit2.http.GET

interface WebService {
    @GET("songs")
    suspend fun getSongs(): Response<SongResponse> // <-- Se solicita SongResponse, NO List<SongBean>
}
```

#### Paso 3: En `viewmodel/MainViewModel.kt` (Extraer la lista interna)

```kotlin
var songList: List<SongBean> by mutableStateOf(emptyList())

suspend fun fetchSongs() {
    val response = RetrofitClient.webService.getSongs()
    if (response.isSuccessful && response.body() != null) {
        // response.body() es SongResponse -> extraemos su lista con .data
        songList = response.body()!!.data
    }
}
```

> 💡 **Nota sobre SQLite:** La base de datos local SQLite solo guarda y lee la lista de `SongBean`. La clase `SongResponse` solo existe para recibir la respuesta HTTP de Retrofit.

---

### B. Caso 2: Objetos Anidados en el mismo registro (Ej: street: { ... })

Ocurre cuando un registro tiene dentro otro objeto con sub-propiedades:

```json
{
  "id": 1,
  "name": "Juan Perez",
  "street": {
    "Adress": "Jr. Arequipa",
    "street": "341"
  }
}
```

#### Paso 1: En `model/beans/UserBean.kt` (Declarar la jerarquía de Data Classes)

```kotlin
package com.example.appname.model.beans

// Sub-clase para el objeto anidado
data class StreetBean(
    val Adress: String = "",
    val street: String = ""
)

// Clase principal que contiene a la sub-clase
data class UserBean(
    val id: Int = 0,
    val name: String = "",
    val street: StreetBean = StreetBean() // <-- Objeto anidado como propiedad
)
```

#### Paso 2: En Jetpack Compose (Cómo mostrarlo en la UI)

Se accede mediante navegación con punto común y corriente:

```kotlin
Text(text = "Nombre: ${user.name}")
Text(text = "Dirección: ${user.street.Adress} N° ${user.street.street}")
```

#### Paso 3: En SQLite (`model/db/OpenHelper.kt`) (Aplanamiento a Columnas Primitivas)

> 📌 **Regla SQLite:** SQLite no almacena objetos anidados. La tabla debe "aplanar" el objeto convirtiendo cada campo anidado en una columna de tipo `TEXT` o `INTEGER`.

```kotlin
package com.example.appname.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appname.model.beans.StreetBean
import com.example.appname.model.beans.UserBean

class OpenHelper(context: Context) : SQLiteOpenHelper(context, "UserDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Columnas separadas para address y street_number
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY,
                name TEXT,
                address TEXT,
                street_number TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // 2. INSERT / GUARDAR: Desglosamos user.street en los ContentValues
    fun insertUser(user: UserBean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", user.id)
            put("name", user.name)
            put("address", user.street.Adress)
            put("street_number", user.street.street)
        }
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // 3. READ / LEER: Reconstruimos el StreetBean desde las columnas
    fun readAllUsers(): MutableList<UserBean> {
        val list = mutableListOf<UserBean>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val streetNum = cursor.getString(cursor.getColumnIndexOrThrow("street_number"))

                val user = UserBean(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    street = StreetBean(Adress = address, street = streetNum) // <-- Reconstrucción
                )
                list.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
```

---

### C. Tabla Comparativa Mnemotécnica

| Formato del JSON en la API | Tipo en Retrofit (`WebService.kt`) | En `MainViewModel` |
| :--- | :--- | :--- |
| `[ { ... }, { ... } ]` *(Array directo en la raíz)* | `Response<List<MiBean>>` | `itemList = response.body()!!` |
| `{ "id": 1, "title": "..." }` *(Objeto directo único)* | `Response<MiBean>` | `item = response.body()!!` |
| `{ "data": [ { ... } ] }` *(Objeto Wrapper con array)* | `Response<MiResponseWrapper>` | `itemList = response.body()!!.data` |
| `{ "id": 1, "sub": { "x": 10 } }` *(Objeto con hijo anidado)* | `Response<MiBean>` *(con `SubBean` adentro)* | `item = response.body()!!` *(y en UI: `item.sub.x`)* |