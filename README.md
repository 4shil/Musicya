# Musicya

An offline music player for Android with a Neo-Brutalist design. Plays local audio files — no streaming, no internet required.

## Features

### Core Playback
- **Local Music Playback** — Plays audio files from device storage
- **Playback Controls** — Play, pause, skip, seek, shuffle, repeat
- **Playback Speed** — Adjust playback from 0.5x to 2.0x
- **Crossfade** — Smooth transitions between tracks
- **Sleep Timer** — Auto-stop playback after set duration with fade-out
- **Audio Focus** — Proper handling of interruptions (calls, notifications)

### Organization & Library
- **Playlists** — Create, edit, merge, export, and import playlists as JSON
- **Smart Playlists** — Recently Added, Most Played, Favorites
- **Genres** — Browse music by detected genre from file paths
- **Folders** — Navigate music by folder structure with breadcrumb support
- **Multi-Select** — Batch operations on multiple songs
- **Duplicate Detection** — Find and remove duplicate songs by name, path, or duration

### Search & Discovery
- **Fuzzy Search** — Find songs even with typos using Levenshtein distance
- **Recent Searches** — Quick access to past queries
- **Albums** — Browse by album with track listings
- **Artists** — View full discography by artist
- **Statistics** — Listening history, play counts, library overview

### User Experience
- **Car Mode** — Large touch targets for safe driving
- **Quick Settings Tile** — Control playback from notification shade
- **Home Screen Widget** — 4x2 widget with album art and playback controls
- **Lock Screen Controls** — Full media controls on lock screen
- **Gesture Support** — Swipe to skip tracks
- **Onboarding** — Feature introduction for new users
- **Haptic Feedback** — Tactile response on interactions

### Customization
- **Equalizer** — 10 presets (Pop, Rock, Jazz, Classical, Hip Hop, Electronic, R&B, Country, Vocal, Flat) + custom 8-band EQ
- **Theme** — Light/dark mode with primary color customization
- **Animated Transitions** — Smooth page and theme transitions

### Data Management
- **Library Import/Export** — Backup and restore library metadata
- **Settings Backup** — Export/import app settings as JSON
- **Folder Sync** — Auto-detect new music files with FileObserver
- **Playlist Export** — Share playlists as JSON files

### Technical
- **Paging 3** — Efficient handling of large music libraries (1000+ songs)
- **Caching** — In-memory and disk caching for album art and metadata
- **Thread-Safe** — Proper concurrency handling with mutex locks
- **Accessibility** — Full TalkBack support with semantic descriptions
- **R8 Optimized** — Code shrinking for smaller APK size

## Tech Stack

- **Kotlin** — Modern Android development
- **Jetpack Compose** — Declarative UI framework
- **Hilt** — Dependency injection
- **Room** — Local SQLite database
- **ExoPlayer / Media3** — Audio playback engine
- **Paging 3** — Efficient large list handling
- **Coil** — Image loading with memory and disk caching
- **Material 3** — Modern design system

**Min SDK:** Android 8.0 (API 26)
**Target SDK:** Android 14 (API 34)

## Building

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK with API 34

### Steps

```bash
git clone https://github.com/4shil/Musicya.git
cd Musicya
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

For release builds:

```bash
./gradlew assembleRelease
```

## Project Structure

```
app/src/main/java/com/fourshil/musicya/
├── MusicyaApp.kt          # Application class with Hilt and crash reporting
├── MainActivity.kt        # Main activity with splash screen
├── data/
│   ├── db/                # Room entities and DAO (Song, Playlist, Favorites, History)
│   ├── model/             # Data models
│   └── repository/        # MusicRepository with caching and paging
├── di/                    # Hilt dependency injection modules
├── player/                # Audio engine, audio focus, crossfade
├── service/               # Media notification, lock screen, background scan
├── ui/
│   ├── album/             # Album detail screen and ViewModel
│   ├── artist/            # Artist detail screen and ViewModel
│   ├── carmode/           # Car mode UI
│   ├── components/        # Shared UI components (mini player, multi-select, etc.)
│   ├── equalizer/         # Equalizer presets and custom bands
│   ├── genre/             # Genre browser
│   ├── library/           # Main library, songs, folders, statistics
│   ├── lyrics/            # Online lyrics fetching
│   ├── navigation/        # Navigation graph and animations
│   ├── nowplaying/        # Full player screen
│   ├── onboarding/        # First-launch onboarding
│   ├── playlist/          # Playlist screens
│   ├── queue/             # Queue management with drag-and-drop
│   ├── search/            # Search with fuzzy matching
│   ├── settings/          # Settings, sleep timer, about
│   ├── theme/             # Colors, typography, Neo-Brutalist design
│   └── widget/            # Home screen widget
└── util/                  # Utilities (album art, backup, cleanup, etc.)
```

## Permissions

| Permission | Purpose |
|---|---|
| `READ_MEDIA_AUDIO` | Read audio files from device storage |
| `READ_EXTERNAL_STORAGE` | Legacy storage access (API < 33) |
| `FOREGROUND_SERVICE` | Keep playback running in background |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Media playback service |
| `WAKE_LOCK` | Prevent CPU sleep during playback |
| `POST_NOTIFICATIONS` | Show playback notifications (Android 13+) |

## Architecture

Musicya follows **Clean Architecture** with proper separation of concerns:

- **UI Layer** — Compose screens with ViewModels and StateFlow
- **Data Layer** — Repositories, Room database, MediaStore access
- **Dependency Injection** — Hilt modules for loose coupling

State is managed reactively using Kotlin `StateFlow` and `collectAsState()`. The player communicates through a `PlayerController` interface, keeping the UI decoupled from ExoPlayer internals.

## License

MIT
