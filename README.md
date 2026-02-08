# Musicya

An offline music player for Android with a Neo-Brutalist design. Plays local audio files — no streaming, no internet required.

![android music player](https://media.giphy.com/media/l0HlBO7eyXzSZkJri/giphy.gif)

## Features

### Core Playback
- **Local Music Playback** — Plays audio files from device storage
- **Playback Controls** — Play, pause, skip, seek, shuffle, repeat
- **Playback Speed** — Adjust playback from 0.5x to 2.0x
- **Crossfade** — Smooth transitions between tracks
- **Sleep Timer** — Auto-stop playback after set duration
- **Audio Focus** — Proper handling of interruptions (calls, notifications)

### Organization & Library
- **Playlists** — Create, edit, merge, export, and import playlists
- **Smart Playlists** — Recently Added, Most Played, Favorites
- **Genres** — Browse music by detected genre from file paths
- **Folders** — Navigate music by folder structure with breadcrumb support
- **Multi-Select** — Batch operations on multiple songs

### Search & Discovery
- **Fuzzy Search** — Find songs even with typos
- **Recent Searches** — Quick access to past queries
- **Albums** — Browse by album with track listings
- **Artists** — View discography by artist

### User Experience
- **Car Mode** — Large touch targets for safe driving
- **Quick Settings Tile** — Control playback from notification shade
- **Home Screen Widget** — 4x2 widget with playback controls
- **Lock Screen Controls** — Full media controls on lock screen
- **Gesture Support** — Swipe to skip tracks
- **Onboarding** — Feature introduction for new users

### Customization
- **Equalizer** — 10 presets + custom 8-band EQ
- **Theme** — Light/dark mode with primary color customization
- **Animated Transitions** — Smooth page and theme transitions

### Data Management
- **Library Import/Export** — Backup and restore library metadata
- **Settings Backup** — Export/import app settings
- **Duplicate Detection** — Find and remove duplicate songs
- **Folder Sync** — Auto-detect new music files

### Technical Features
- **Paging** — Efficient handling of large music libraries (1000+ songs)
- **Caching** — In-memory and disk caching for fast loading
- **Thread-Safe** — Proper concurrency handling
- **Accessibility** — Full TalkBack support with semantic descriptions

## Tech Stack

- **Kotlin** — Modern Android development
- **Jetpack Compose** — Declarative UI framework
- **Hilt** — Dependency injection
- **Room** — Local SQLite database
- **ExoPlayer / Media3** — Audio playback engine
- **Paging 3** — Efficient large list handling
- **Material 3** — Modern design system

**Min SDK:** Android 8.0 (API 26)  
**Target SDK:** Android 14 (API 34)

## What's New in v1.1.0

### Enhanced Playback
- Audio focus management for proper interruption handling
- Lock screen media controls with full playback state
- Quick Settings tile for notification shade control
- Playback history and statistics tracking

### New Screens
- **Car Mode** — Safe driving with extra-large controls
- **Genre Browser** — Music organized by detected genre
- **Album Detail** — Track listings with disc info
- **Artist Detail** — Discography view
- **Statistics** — Listening history and library overview
- **Lyrics** — Online lyrics fetching

### Improved Organization
- Folder breadcrumb navigation
- Playlist merge utility
- Multi-select batch operations
- Drag-and-drop queue reordering
- Smart shuffle around current track

### Better Search
- Fuzzy search with Levenshtein distance
- Recent searches history
- Results sorted by relevance

### System Integration
- Home screen widget with album art
- App shortcuts (Shuffle All, Random, etc.)
- Deep links for sharing and navigation
- Queue persistence across restarts

### UI/UX Polish
- Animated theme transitions
- Haptic feedback on interactions
- Enhanced onboarding flow
- Error states with retry actions
- Splash screen for faster perceived startup

## Building

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK with API 34

### Steps

1. Clone the repository:

```bash
git clone https://github.com/4shil/Musicya.git
```

2. Open the project in Android Studio.

3. Sync Gradle dependencies.

4. Run on a device or emulator (API 26+).

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project Structure

```
app/src/main/java/com/fourshil/musicya/
├── data/
│   ├── db/             # Room entities and DAO (Song, Playlist, Favorites, History)
│   ├── model/          # Data models
│   └── repository/     # MusicRepository, paging source
├── di/                 # Hilt modules
├── player/             # Audio engine, audio focus, crossfade
├── service/            # Media notification, lock screen, background scan
├── ui/
│   ├── album/          # Album detail screen
│   ├── artist/         # Artist detail screen
│   ├── carmode/        # Car mode UI
│   ├── equalizer/      # Equalizer presets and custom bands
│   ├── genre/          # Genre browser
│   ├── library/        # Main library, songs, folders, statistics
│   ├── lyrics/         # Online lyrics fetching
│   ├── nowplaying/     # Full player screen
│   ├── onboarding/     # First-launch onboarding
│   ├── playlist/       # Playlist screens
│   ├── queue/          # Queue management
│   ├── search/         # Search with fuzzy matching
│   ├── settings/       # Settings, sleep timer, about
│   └── theme/          # Colors, typography, Neo-Brutalist design
├── util/               # Utilities (album art, backup, cleanup, etc.)
└── widget/             # Home screen widget
```

## Permissions

- `READ_MEDIA_AUDIO` — Read audio files from device storage
- `READ_EXTERNAL_STORAGE` (API < 33) — Legacy storage access
- `FOREGROUND_SERVICE` — Keep playback running in background
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` — Media playback service
- `WAKE_LOCK` — Prevent CPU sleep during playback
- `RECEIVE_BOOT_COMPLETED` — Restore widget state on reboot
- `POST_NOTIFICATIONS` — Show playback notifications (Android 13+)

## Architecture

Musicya follows **Clean Architecture** with proper separation:

- **UI Layer** — Compose screens and ViewModels
- **Domain Layer** — Use cases and business logic
- **Data Layer** — Repositories, Room database, MediaStore access

## License

MIT