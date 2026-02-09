# Changelog

All notable changes to Musicya will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.2.0] - 2026-02-09

### Added
- Audio focus management for proper interruption handling
- Lock screen media controls with full playback state
- Quick Settings tile for notification shade control
- Car Mode screen with extra-large touch targets
- Genre Browser for music organization
- Album Detail screen with track listings
- Artist Detail screen with discography
- Statistics screen with listening history
- Lyrics screen with online fetching
- Home screen widget (4x2)
- App shortcuts (Shuffle All, Random, etc.)
- Deep link handling for sharing
- Queue save/restore across sessions
- Multi-select mode with batch operations
- Playlist merge utility
- Folder sync manager
- Equalizer with 10 presets and custom bands
- Theme customization with color options
- Sleep timer dialog with presets
- Backup/restore system
- Duplicate detection
- Fuzzy search with typo tolerance
- Recent searches history
- Performance monitoring utilities
- Unit tests for ViewModels
- Integration tests for repository
- UI tests for navigation

### Changed
- Updated version from 1.0 to 1.2.0
- Enabled R8 code shrinking
- Optimized paging for large libraries
- Improved search debounce timing (150ms)
- Enhanced notification media style
- Updated README with comprehensive documentation
- Added CONTRIBUTING.md for developers

### Fixed
- Memory leak in ViewModels
- Cache not invalidating properly
- Search not clearing on navigation
- Queue not persisting on restart
- Notification not updating on track change

### Removed
- Legacy MediaStore query code
- Unused debug logging (in release)

## [1.0.0] - 2024-01-15

### Added
- Initial release with core playback
- Local music library scanning
- Playlists and favorites
- Basic search functionality
- Now Playing screen
- Settings screen
- Material 3 Neo-Brutalist design