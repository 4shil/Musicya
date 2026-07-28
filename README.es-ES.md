# Musicya

Un reproductor de música offline para Android con un diseño Neo-Brutalista. Reproduce archivos de audio locales; sin streaming, no requiere internet.

## Características

### Reproducción Principal
- **Reproducción de Música Local** — Reproduce archivos de audio desde el almacenamiento del dispositivo.
- **Controles de Reproducción** — Reproducir, pausar, saltar, buscar, aleatorio, repetir.
- **Velocidad de Reproducción** — Ajusta la velocidad de 0.5x a 2.0x.
- **Crossfade** — Transiciones suaves entre pistas.
- **Temporizador de Apagado** — Detiene la reproducción automáticamente tras una duración establecida con desvanecimiento (fade-out).
- **Foco de Audio** — Manejo adecuado de interrupciones (llamadas, notificaciones).

### Organización y Biblioteca
- **Listas de Reproducción** — Crea, edita, fusiona, exporta e importa listas de reproducción como JSON.
- **Listas Inteligentes** — Añadidos recientemente, más reproducidos, favoritos.
- **Géneros** — Explora la música por género detectado desde las rutas de los archivos.
- **Carpetas** — Navega por la música mediante la estructura de carpetas con soporte de migas de pan (breadcrumbs).
- **Selección Múltiple** — Operaciones por lote en múltiples canciones.
- **Detección de Duplicados** — Encuentra y elimina canciones duplicadas por nombre, ruta o duración.

### Búsqueda y Descubrimiento
- **Búsqueda Difusa (Fuzzy Search)** — Encuentra canciones incluso con errores tipográficos usando la distancia de Levenshtein.
- **Búsquedas Recientes** — Acceso rápido a consultas pasadas.
- **Álbumes** — Explora por álbum con listado de pistas.
- **Artistas** — Mira la discografía completa por artista.
- **Estadísticas** — Historial de escucha, recuentos de reproducción, vista general de la biblioteca.

### Experiencia de Usuario
- **Modo Coche** — Objetivos táctiles grandes para una conducción segura.
- **Mosaico de Ajustes Rápidos** — Controla la reproducción desde el panel de notificaciones.
- **Widget de Pantalla de Inicio** — Widget de 4x2 con arte del álbum y controles de reproducción.
- **Controles de Pantalla de Bloqueo** — Controles multimedia completos en la pantalla de bloqueo.
- **Soporte de Gestos** — Desliza para saltar pistas.
- **Onboarding** — Introducción de características para nuevos usuarios.
- **Retroalimentación Háptica** — Respuesta táctil en las interacciones.

### Personalización
- **Ecualizador** — 10 ajustes preestablecidos (Pop, Rock, Jazz, Classical, Hip Hop, Electronic, R&B, Country, Vocal, Flat) + EQ personalizado de 8 bandas.
- **Tema** — Modo claro/oscuro con personalización del color primario.
- **Transiciones Animadas** — Transiciones suaves de página y tema.

### Gestión de Datos
- **Importación/Exportación de Biblioteca** — Copia de seguridad y restauración de metadatos de la biblioteca.
- **Copia de Seguridad de Ajustes** — Exporta/importa los ajustes de la aplicación como JSON.
- **Sincronización de Carpetas** — Detecta automáticamente nuevos archivos de música con FileObserver.
- **Exportación de Listas** — Comparte listas de reproducción como archivos JSON.

### Técnico
- **Paging 3** — Manejo eficiente de bibliotecas de música grandes (1000+ canciones).
- **Caché** — Caché en memoria y disco para el arte de los álbumes y metadatos.
- **Thread-Safe** — Manejo adecuado de la concurrencia con bloqueos de mutex.
- **Accesibilidad** — Soporte completo de TalkBack con descripciones semánticas.
- **Optimizado con R8** — Reducción de código para un tamaño de APK más pequeño.

## Stack Tecnológico

- **Kotlin** — Desarrollo moderno de Android.
- **Jetpack Compose** — Framework de UI declarativa.
- **Hilt** — Inyección de dependencias.
- **Room** — Base de datos SQLite local.
- **ExoPlayer / Media3** — Motor de reproducción de audio.
- **Paging 3** — Manejo eficiente de listas grandes.
- **Coil** — Carga de imágenes con caché de memoria y disco.
- **Material 3** — Sistema de diseño moderno.

**Min SDK:** Android 8.0 (API 26)
**Target SDK:** Android 14 (API 34)

## Construcción

### Requisitos Previos

- Android Studio Hedgehog o superior.
- JDK 17.
- Android SDK con API 34.

### Pasos

```bash
git clone https://github.com/4shil/Musicya.git
cd Musicya
./gradlew assembleDebug
```

El APK de depuración estará en `app/build/outputs/apk/debug/app-debug.apk`.

Para builds de lanzamiento:

```bash
./gradlew assembleRelease
```

## Estructura del Proyecto

```
app/src/main/java/com/fourshil/musicya/
├── MusicyaApp.kt          # Clase de aplicación con Hilt y reporte de errores
├── MainActivity.kt        # Actividad principal con pantalla de inicio (splash screen)
├── data/
│   ├── db/                # Entidades de Room y DAO (Song, Playlist, Favorites, History)
│   ├── model/             # Modelos de datos
│   └── repository/        # MusicRepository con caché y paginación
├── di/                    # Módulos de inyección de dependencias de Hilt
├── player/                # Motor de audio, foco de audio, crossfade
├── service/               # Notificación de medios, pantalla de bloqueo, escaneo en segundo plano
├── ui/
│   ├── album/             # Pantalla de detalle de álbum y ViewModel
│   ├── artist/            # Pantalla de detalle de artista y ViewModel
│   ├── carmode/           # UI de modo coche
│   ├── components/        # Componentes de UI compartidos (mini reproductor, selección múltiple, etc.)
│   ├── equalizer/         # Ajustes preestablecidos del ecualizador y bandas personalizadas
│   ├── genre/             # Navegador de géneros
│   ├── library/           # Biblioteca principal, canciones, carpetas, estadísticas
│   ├── lyrics/            # Obtención de letras en línea
│   ├── navigation/        # Grafo de navegación y animaciones
│   ├── nowplaying/        # Pantalla completa del reproductor
│   ├── onboarding/        # Onboarding de primer inicio
│   ├── playlist/          # Pantallas de listas de reproducción
│   ├── queue/             # Gestión de cola con arrastrar y soltar
│   ├── search/            # Búsqueda con coincidencia difusa
│   ├── settings/          # Ajustes, temporizador de apagado, acerca de
│   ├── theme/             # Colores, tipografía, diseño Neo-Brutalista
│   └── widget/            # Widget de pantalla de inicio
└── util/                  # Utilidades (arte de álbum, copia de seguridad, limpieza, etc.)
```

## Permisos

| Permiso | Propósito |
|---|---|
| `READ_MEDIA_AUDIO` | Leer archivos de audio del almacenamiento del dispositivo |
| `READ_EXTERNAL_STORAGE` | Acceso al almacenamiento heredado (API < 33) |
| `FOREGROUND_SERVICE` | Mantener la reproducción ejecutándose en segundo plano |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Servicio de reproducción de medios |
| `WAKE_LOCK` | Evitar que la CPU entre en modo reposo durante la reproducción |
| `POST_NOTIFICATIONS` | Mostrar notificaciones de reproducción (Android 13+) |

## Arquitectura

Musicya sigue la **Clean Architecture** con una separación adecuada de responsabilidades:

- **Capa de UI** — Pantallas de Compose con ViewModels y StateFlow.
- **Capa de Datos** — Repositorios, base de datos Room, acceso a MediaStore.
- **Inyección de Dependencias** — Módulos de Hilt para un acoplamiento débil.

El estado se gestiona de forma reactiva utilizando `StateFlow` de Kotlin y `collectAsState()`. El reproductor se comunica a través de una interfaz `PlayerController`, manteniendo la UI desacoplada de los detalles internos de ExoPlayer.

## Licencia

MIT
