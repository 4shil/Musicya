# Code Review Checklist - Musicya

## Before Merging

### Code Quality
- [ ] Code follows Kotlin coding conventions
- [ ] No hardcoded strings (use string resources)
- [ ] Proper error handling with try-catch
- [ ] No memory leaks (check ViewModels, coroutines)
- [ ] No force unwraps (?. or if-null checks)
- [ ] No TODO/FIXME in production code

### Testing
- [ ] Unit tests for new ViewModels
- [ ] Integration tests for new repositories
- [ ] UI tests for new screens
- [ ] All existing tests pass
- [ ] New features have test coverage

### Performance
- [ ] No heavy operations on main thread
- [ ] Proper use of LazyColumn/LazyGrid
- [ ] Image loading uses Coil with caching
- [ ] Large lists use Paging 3
- [ ] Debounce search input
- [ ] No unnecessary recomposition

### Security
- [ ] No sensitive data in logs
- [ ] No hardcoded credentials
- [ ] Permissions are justified
- [ ] External input is validated

### Accessibility
- [ ] All images have contentDescription
- [ ] Touch targets are at least 48dp
- [ ] Color contrast is sufficient
- [ ] Screen reader compatible

### UI/UX
- [ ] Follows Neo-Brutalist design
- [ ] Consistent spacing (use NeoDimens)
- [ ] Loading states for async operations
- [ ] Error states with retry options
- [ ] Empty states with helpful messages

### Documentation
- [ ] Public APIs are documented
- [ ] README is updated for new features
- [ ] CHANGELOG reflects changes
- [ ] RELEASE_NOTES are complete

### Commits
- [ ] Descriptive commit messages (no prefixes)
- [ ] One logical change per commit
- [ ] All commits are signed off

## Before Release

### Build
- [ ] Debug APK builds successfully
- [ ] Release APK builds with R8
- [ ] No lint warnings (or ignored appropriately)
- [ ] Version updated in build.gradle

### Verification
- [ ] Run verification script: `./scripts/verify_build.sh`
- [ ] Check all 6 permission checks pass
- [ ] Test on at least one physical device
- [ ] Test on different Android versions

### Play Store
- [ ] Screenshots captured
- [ ] Play Store listing draft complete
- [ ] Privacy policy drafted
- [ ] Release notes written

### Backup
- [ ] Repository backed up
- [ ] Release branch created
- [ ] GitHub release drafted

## Release Process

1. Update version in build.gradle.kts
2. Update CHANGELOG.md
3. Update RELEASE_NOTES.md
4. Update README.md if needed
5. Run full test suite
6. Build release APK
7. Verify APK with zipalign
8. Sign APK (if not using Play Signing)
9. Upload to Play Store
10. Create GitHub release
11. Announce (if applicable)