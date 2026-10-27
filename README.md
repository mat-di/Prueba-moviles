# Sistema de Tickets (Android · Kotlin + Java)

App para registrar tickets de soporte y gestionar documentos PDF y Word.
Hecha con Jetpack Compose, ViewModel y APIs nativas de archivos.
Los datos se mantienen en memoria mientras la app está abierta (sin base de datos).

## Cómo ejecutarla

1. Abrir la carpeta en Android Studio y esperar el *Gradle Sync*.
2. Elegir un emulador o teléfono con Android 13 (API 33) o superior y presionar ▶ Run.
3. En la app: **Crear cuenta** (el correo debe terminar en `@duoc.cl`) e iniciar sesión.

Para probar la carga de documentos en el emulador, arrastra un PDF o un .docx desde el PC a
la ventana del emulador: queda en *Descargas* y se puede elegir desde **Cargar**.

## Requisitos y dónde se cumplen

| Requisito | Dónde está |
|---|---|
| Formularios validados | `util/Validaciones.kt` (reglas) y los formularios de login, registro, nuevo ticket y carga de documentos. Los errores se muestran bajo cada campo. |
| Navegación funcional | `AppNavegacion.kt` (NavHost con rutas en `Rutas.kt`). Las rutas internas están protegidas: sin sesión, vuelve al login. |
| Gestión de estado | `viewmodel/` — `AuthViewModel`, `TicketViewModel`, `DocumentoViewModel` con `StateFlow`. |
| Almacenamiento | `data/Almacen.kt` guarda usuarios, tickets y documentos en memoria. Los PDF y Word se copian a la caché de la app. |
| Recursos nativos (PDF, Word) | `archivos/ArchivoManager.kt`: selector de archivos del sistema, copia interna, abrir con otra app y compartir (FileProvider). `archivos/RenderizadorPdf.kt` (PdfRenderer), `archivos/GeneradorPdf.kt` (PdfDocument) y `archivos/LectorWord.kt` (lee .docx). |
| Menú | Menú lateral (`MenuLateral`) y menú del usuario en la barra superior (`ui/componentes/BarrasNavegacion.kt`). |
| Barra superior e inferior | `BarraSuperior` y `BarraInferior` en `ui/componentes/BarrasNavegacion.kt`. |
| Registrar usuarios | `ui/pantallas/PantallaRegistro.kt` |
| Iniciar sesión | `ui/pantallas/LoginScreen.kt` |
| Cierre de sesión | `ui/pantallas/PantallaCerrarSesion.kt` |
| Cargar documentos | `ui/pantallas/PantallaCargar.kt` (y "Adjuntar" desde el detalle de un ticket). Incluye el tipo "Rúbrica" para la siguiente etapa. |
| Visualizar documentos | `ui/pantallas/PantallaVisualizar.kt` (lista) y `ui/pantallas/PantallaVisor.kt` (PDF y .docx dentro de la app). |
| Eliminar documentos | `ui/pantallas/PantallaEliminar.kt` (con confirmación). |

## Estructura

```
com.example.sistematicket
├── MainActivity.kt / AppNavegacion.kt / Rutas.kt
├── data/        Usuario, Ticket, Documento, Catalogo, Almacen · Seguridad.java (Java)
├── archivos/    ArchivoManager, RenderizadorPdf, GeneradorPdf, LectorWord
├── util/        Validaciones, Formato
├── viewmodel/   AuthViewModel, TicketViewModel, DocumentoViewModel
└── ui/
    ├── componentes/  Barras, menú, campos de texto, tarjetas, visores
    └── pantallas/    Una pantalla por archivo
```

Las contraseñas se guardan cifradas con SHA-256 y sal (`Seguridad.java`), nunca en texto plano.

## Pruebas

```
./gradlew testDebugUnitTest
```

`ValidacionesTest` y `SeguridadTest` revisan las reglas de los formularios y el cifrado de contraseñas.
