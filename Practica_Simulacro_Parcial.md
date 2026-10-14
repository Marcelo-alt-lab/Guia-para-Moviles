# Evaluación Práctica de Desarrollo Móvil (Simulacro Tipo Examen)
**Curso:** Aplicaciones para Dispositivos Móviles (1ACC0238)  
**Tiempo estimado:** 90 - 120 minutos  
**Tecnologías:** Kotlin, Jetpack Compose (Material 3), Retrofit 2, SQLite (`SQLiteOpenHelper`), `SharedPreferences`, Navigation Compose.

---

## Enunciado del Caso: PostManager App

Se solicita desarrollar una aplicación móvil nativa en Android con Jetpack Compose denominada **PostManager**, orientada a la consulta, guardado local y edición de artículos mediante 3 pantallas conectadas por Navigation Compose:

---

### Pantalla 1: Consulta y Registro (Retrofit -> SQLite)
- **Control de entrada:** `OutlinedTextField` numérico para ingresar el `id` del post.
- **Botón "Consultar":** Consume la API vía Retrofit (`GET https://jsonplaceholder.typicode.com/posts/{id}`) usando corrutinas (`Dispatchers.IO`).
- **Vista de resultados:** Muestra en pantalla el `id`, `title` y `body` del post obtenido.
- **Botón "Guardar":** Almacena el post consultado en la base de datos local SQLite (`PostDb.db`).
- **Navegación:** Botón o acceso para pasar a la **Pantalla 2 (Lista)**.

---

### Pantalla 2: Listado y Gestión Local (SQLite + SharedPreferences)
- **Lista reactiva:** `LazyColumn` que carga todos los registros almacenados en SQLite.
- **Estructura de cada Tarjeta (`Card`):**
  - Muestra la información del post (`id`, `title`, `body`).
  - **Botón/Ícono `[X]` (Eliminar):** Borra el registro inmediatamente de la base de datos SQLite y refresca la lista.
  - **Botón/Ícono `[E]` (Editar):** Navega hacia la **Pantalla 3 (Edición)** enviando o seleccionando el post correspondiente.
- **Footer de Totales (SharedPreferences):**
  - Al pie de la lista se muestra un texto con el **Total de registros guardados** (ej. `TOTAL: 4`).
  - Dicho contador debe persistirse en `SharedPreferences` y actualizarse cada vez que se agregue, edite o elimine un registro.

---

### Pantalla 3: Edición de Registro (Update SQLite)
- **Campos editables:** 
  - Muestra el `ID` (de solo lectura).
  - `OutlinedTextField` para editar el `title`.
  - `OutlinedTextField` para editar el `body`.
- **Botón "Editar / Actualizar":**
  - Actualiza los datos modificados en la tabla SQLite (`db.update`).
  - Actualiza el estado en el ViewModel y SharedPreferences.
  - Redirige de regreso a la **Pantalla 2 (Lista)** reflejando los cambios.

---

## Especificaciones Técnicas y Endpoint

- **Endpoint Retrofit:**  
  `Base URL:` `https://jsonplaceholder.typicode.com/`  
  `Path:` `posts/{id}`  
  `Formato JSON:`
  ```json
  {
    "userId": 1,
    "id": 1,
    "title": "sunt aut facere repellat...",
    "body": "quia et suscipit..."
  }
  ```
- **Persistencia:** SQLite nativo obligatorio con `SQLiteOpenHelper` (no Room).
- **Contador:** `SharedPreferences` con clave `"total_count"`.

---

## Rúbrica de Calificación (20 Puntos)

| Criterio | Descripción | Puntaje |
| :--- | :--- | :---: |
| **C01. Build & Ejecución** | El proyecto compila y se despliega sin errores en el emulador. | **2.0 pts** |
| **C02. Navegación (3 Vistas)** | Flujo funcional entre Consulta (P1), Lista (P2) y Edición (P3) con Navigation Compose. | **3.0 pts** |
| **C03. Networking (Retrofit)** | Consulta por ID funcional con Retrofit, corrutinas y Gson en Pantalla 1. | **4.0 pts** |
| **C04. SQLite CRUD Nativo** | Implementación completa de `insert`, `query`, `delete` y `update` con `OpenHelper`. | **5.0 pts** |
| **C05. SharedPreferences** | Almacenamiento y actualización reactiva del contador `TOTAL` al pie de la lista. | **3.0 pts** |
| **C06. Arquitectura y Nomenclatura** | Estructura en capas MVVM y código 100% en inglés (`PostViewModel`, `OpenHelper`, etc.). | **3.0 pts** |
| **TOTAL** | | **20.0 pts** |
