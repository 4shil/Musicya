# Musicya v1.2.0 Release Notes

**Release Date:** February 2026

## New Features

### Playback Enhancements
- **Audio Focus Management** — Proper handling of phone calls and other interruptions
- **Lock Screen Controls** — Full media controls available from lock screen
- **Quick Settings Tile** — Control playback directly from notification shade
- **Playback Speed Control** — Adjust playback from 0.5x to 2.0x
- **Crossfade** — Smooth transitions between tracks

### New Screens
- **Car Mode** — Extra-large controls for safe driving
- **Genre Browser** — Navigate music by detected genre
- **Album Detail** — View album tracks with disc information
- **Artist Detail** — Browse discography by artist
- **Statistics** — Listening history and library overview
- **Lyrics** — Online lyrics fetching with fallback

### Organization
- **Multi-Select** — Select and batch operate on multiple songs
- **Playlist Merge** — Combine playlists intelligently
- **Queue Management** — Drag-and-drop reordering
- **Folder Sync** — Auto-detect new music files

### Search
- **Fuzzy Search** — Find songs even with typos
- **Recent Searches** — Quick access to past queries
- **Relevance Sorting** — Best matches first

### System Integration
- **Home Screen Widget** — 4x2 widget with album art and controls
- **App Shortcuts** — Quick actions from launcher
- **Deep Links** — Share and navigate via URLs

### Customization
- **Equalizer** — 10 presets + custom 8-band EQ
- **Theme** — Light/dark mode with color customization
- **Backup/Restore** — Export and import settings

## Improvements

### Performance
- Paging for large libraries (1000+ songs)
- Memory and disk caching
- Thread-safe song loading
- Optimized album art loading

### UI/UX
- Animated theme transitions
- Haptic feedback on interactions
- Error states with retry options
- Splash screen for faster startup
- Enhanced onboarding flow

### Technical
- R8 code shrinking enabled
- Comprehensive unit tests
- Integration tests for repository
- UI tests for navigation

## Bug Fixes

- Fixed crash when no songs in library
- Fixed search not clearing on back navigation
- Fixed queue not persisting across app restart
- Fixed memory leak in ViewModels
- Fixed notification not updating on track change
- Fixed crossfade not working on first play

## Known Issues

- Chromecast support is placeholder (UI ready, backend pending)
- Some OEM devices may require additional permissions
- Large playlists (1000+ songs) may have slow scrolling

## Permissions

- `READ_MEDIA_AUDIO` — Read audio files
- `FOREGROUND_SERVICE` — Background playback
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` — Media service
- `WAKE_LOCK` — Prevent CPU sleep
- `RECEIVE_BOOT_COMPLETED` — Widget restoration
- `POST_NOTIFICATIONS` — Android 13+ notifications

## Requirements

- **Min SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)
- **Processor:** ARM64 recommended
- **Storage:** 50MB minimum

## Migrating from v1.1.0

No breaking changes. Simply install over existing app.

## Credits

- **Development:** Ashil
- **Design:** Neo-Brutalist inspired
- **Libraries:** Jetpack Compose, Media3, Room, Hilt