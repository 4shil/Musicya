# Changelog

All notable changes to Musicya are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.2.0] - 2026-02-09

### Added
- Audio focus management for proper interruption handling (phone calls, notifications)
- Lock screen media controls with full playback state display
- Quick Settings tile for notification shade playback control
- Car Mode screen with extra-large touch targets for safe driving
- Genre Browser for music organization by detected genre
- Album Detail screen with track listing and sort options
- Artist Detail screen with discography view
- Statistics screen with listening history and library overview
- Lyrics screen with online fetching and error handling
- Home screen widget (4x2) with album art and playback controls
- App shortcuts (Shuffle All, Random Play, etc.)
- Deep link handling for sharing and navigation
- Queue save/restore across app sessions
- Multi-select mode with batch operations (delete, add to playlist)
- Playlist merge utility to combine multiple playlists
- Folder sync manager with FileObserver for auto-detecting new files
- Equalizer with 10 presets and custom 8-band editor
- Theme customization with color options and animated transitions
- Sleep timer dialog with 8 presets and custom duration input
- Backup/restore system for settings and library metadata
- Duplicate song detection by name+artist, path, or duration+size
- Fuzzy search with Levenshtein distance for typo tolerance
- Recent searches history with quick access
- Performance monitoring and profiling utilities
- Crash reporting stub for production monitoring
- Analytics stub for user interaction tracking
- Unit tests for ViewModels (SearchViewModel, QueueViewModel)
- Integration tests for MusicRepository with mocked MediaStore
- UI tests for navigation flows
- GitHub Actions CI workflow for automated testing
- Build verification script for release validation
- R8 code shrinking configuration for smaller APK size
- Comprehensive accessibility content descriptions

### Changed
- Updated version from 1.0.0 to 1.2.0 (versionCode 2)
- Optimized PagingConfig for better large library performance
- Improved search debounce timing from 300ms to 150ms
- Enhanced notification with MediaStyle and action buttons
- Updated README with comprehensive documentation
- Added CONTRIBUTING.md for developer guidelines

### Fixed
- Memory leak in ViewModels (added onCleared cleanup)
- Cache invalidation not working properly
- Search results not clearing on navigation
- Queue not persisting on app restart
- Notification not updating on track change

## [1.0.0] - 2024-01-15

### Added
- Initial release
- Local music playback with ExoPlayer/Media3
- Library scanning via MediaStore
- Playlists and favorites management
- Basic search functionality
- Now Playing screen with album art
- Settings screen with theme toggle
- Material 3 Neo-Brutalist design