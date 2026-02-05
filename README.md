# Musicya

An offline music player for Android with a Neo-Brutalist design. Plays local audio files — no streaming, no internet required.

![android music player](https://media.giphy.com/media/l0HlBO7eyXzSZkJri/giphy.gif)

## Features

- Plays local audio files from device storage
- Playlists and favorites
- Synced lyrics (LRC file support)
- Album art display
- Equalizer with presets
- Playback speed control
- Crossfade between tracks
- Sleep timer
- Home screen widget
- Search across your library

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Hilt (dependency injection)
- Room (local database)
- ExoPlayer / MediaSession (audio engine)
- Material 3

**Min SDK:** Android 8.0 (API 26)  
**Target SDK:** Android 14 (API 34)

## What's New in v1.1.0

- Pull-to-refresh library scanning
- Improved accessibility with content descriptions on all icons
- Proper back navigation from NowPlayingScreen
- Crossfade engine fixes and initialization
- Gradient rendering performance improvements
- Snackbar error handling for library loading
- Settings screen accessibility improvements

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

4. Run on a device or emulator (API 24+).

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
├── player/             # Audio engine, crossfade, sleep timer, playback speed
├── ui/
│   ├── theme/          # Colors, typography, dimensions
│   ├── settings/       # Settings and equalizer screens
│   ├── search/         # Search screen and ViewModel
│   └── widget/         # Home screen widget
└── util/               # Lyrics parser, album art helpers
```

## Permissions

- `READ_MEDIA_AUDIO` — to read audio files from device storage
- `FOREGROUND_SERVICE` — to keep playback running in the background
- `RECEIVE_BOOT_COMPLETED` — to restore widget state on reboot

## License

MIT
