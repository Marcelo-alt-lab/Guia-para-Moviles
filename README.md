# 📱 Guía para Móviles (Android con Jetpack Compose)

Repositorio de preparación y simulacros para el examen parcial de **Desarrollo de Aplicaciones Móviles (1ACC0238)** en Android con Kotlin, Jetpack Compose, Retrofit 2, SQLite (`SQLiteOpenHelper`) y `SharedPreferences`.

---

## ⚡ Creación Automática de Carpetas (Terminal de Android Studio)

Para crear toda la arquitectura limpia de paquetes (**MVVM**) en un solo paso desde la terminal integrada de Android Studio (Terminal tab abajo), copia y pega el comando correspondiente según tu sistema operativo.

> 💡 **Nota:** Reemplaza `appname` por el nombre de tu paquete (ejemplo: `appbeauty`, `postmanager`, `songapp`, etc.).

### 🪟 Windows (PowerShell - Terminal por defecto en Android Studio Windows)

```powershell
$pkg = "app/src/main/java/com/example/appname"; "model/beans", "model/client", "model/db", "model/response", "viewmodel", "views/nav" | ForEach-Object { New-Item -ItemType Directory -Path "$pkg/$_" -Force }
```

*Si usas Command Prompt (CMD) en Windows:*
```cmd
set PKG=app\src\main\java\com\example\appname
mkdir %PKG%\model\beans %PKG%\model\client %PKG%\model\db %PKG%\model\response %PKG%\viewmodel %PKG%\views\nav
```

### 🍎 macOS / 🐧 Linux (Bash / Zsh)

```bash
PKG="app/src/main/java/com/example/appname" && mkdir -p "$PKG"/model/{beans,client,db,response} "$PKG"/viewmodel "$PKG"/views/nav
```

---

### 📂 Estructura generada:

```text
app/src/main/java/com/example/appname/
├── model/
│   ├── beans/       -> Data Classes Kotlin (modelos de API y BD)
│   ├── client/      -> RetrofitClient (Singleton con baseUrl)
│   ├── db/          -> OpenHelper (SQLiteOpenHelper con CRUD completo nativo)
│   └── response/    -> WebService (Interfaces @GET con @Query o @Path)
├── viewmodel/       -> ViewModel (mutableStateOf, Corrutinas y SharedPreferences)
└── views/
    ├── nav/         -> NavHost y control de navegación
    ├── SearchView   -> Pantalla de búsqueda (API Retrofit + LazyColumn)
    └── ListView     -> Pantalla de listado local (SQLite + Acciones)
```

---

## 📚 Contenido del Repositorio

### 1. Proyectos Android Prácticos
- **`AppBeauty/`**: Caso de examen oficial (2025-02).
  - Consulta de productos de belleza por categoría vía Retrofit (`beauty_products.php?categoria=...`).
  - Navegación con `ModalNavigationDrawer` (Material 3) y Branding.
  - Diálogo modal flotante (`AlertDialog`) con detalle para agregar a favoritos en SQLite.
  - Lista de favoritos con edición de precio y borrado directo.
  - Cálculo dinámico de **Suma Total e IGV (18%)** persistido en `SharedPreferences`.
- **`PostManager/`**: Simulacro basado exactamente en la arquitectura de pizarra de clase.
  - Consulta de posts por ID (`posts/{id}`).
  - Guardado en SQLite (`PostDB.db`).
  - Lista con dos botones de acción: `[X]` (Eliminar directo) y `[E]` (Editar post en Pantalla 3).
  - Pantalla 3 (`EditPost`) con formulario de edición y actualización en BD.
  - Contador reactivo `TOTAL: X` persistido en `SharedPreferences`.
- **`PostManagerLoginCase/`**: Variante del simulacro con flujo completo de **Autenticación / Inicio de Sesión**:
  - Pantalla `LoginPost` con campos de usuario y contraseña (`PasswordVisualTransformation`).
  - Persistencia de sesión en `SharedPreferences` (`is_logged`, `username`).
  - Redirección con limpieza de pila (`popUpTo`) evitando regresar al login con el botón Atrás.
  - Arranque dinámico según estado de sesión (`startDestination = if (isUserLoggedIn) "FindPost" else "LoginPost"`).
  - Botón de `Cerrar Sesión` para limpiar credenciales y volver al login.

### 2. Guías y Documentación de Estudio
- **[Guia presuntamente maestra.md](Guia%20presuntamente%20maestra.md)**: Manual integral con código genérico reutilizable (Ctrl+F), bloques modulares de UI, explicación paso a paso de cada capa y la **Sección 11 de Solución de Errores y Trampas de Examen (Gradle JDK, SQLite, Scroll, etc.)**.
- **[Pasos para Moviles.md](Pasos%20para%20Moviles.md)**: Plantilla secuencial detallada con checklist final para asegurar 20/20 según la rúbrica oficial.
- **[Profesor.md](Profesor.md)**: Prompt para IA con la rúbrica oficial desglosada (9 criterios de evaluación) y las normas de calificación.
- **[Practica_Simulacro_Parcial.md](Practica_Simulacro_Parcial.md)**: Enunciado formal del caso de la pizarra con su rúbrica sobre 20 puntos.
