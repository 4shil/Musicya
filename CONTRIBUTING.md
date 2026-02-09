# Contributing to Musicya

Thank you for your interest in contributing to Musicya! This document outlines the process for contributing.

## Getting Started

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Run tests: `./gradlew test`
5. Commit with descriptive messages (no prefixes like feat:, fix:)
6. Push and open a Pull Request

## Code Style

- Follow Kotlin coding conventions
- Use Jetpack Compose for all UI
- Prefer immutable data structures
- Use dependency injection with Hilt
- Write unit tests for ViewModels and utilities

## Commit Message Format

Use descriptive, plain English messages:

```
Add fuzzy search with Levenshtein distance matching

- Implement FuzzySearch utility class
- Add scoring from 0.0 to 1.0
- Integrate with SearchViewModel
- Add unit tests for edge cases
```

Do NOT use prefixes like `feat:`, `fix:`, `chore:`, `docs:`.

## Testing

- Unit tests: `app/src/test/`
- Integration tests: `app/src/androidTest/`
- Run all tests: `./gradlew test connectedAndroidTest`

## Pull Request Process

1. Update documentation if needed
2. Add tests for new features
3. Ensure all tests pass
4. Update RELEASE_NOTES.md
5. Request review from maintainers

## Code Review Checklist

- [ ] Code follows project conventions
- [ ] Tests are included and passing
- [ ] No hardcoded strings (use resources)
- [ ] Proper error handling
- [ ] Accessibility labels added
- [ ] No memory leaks
- [ ] Performance considered for large lists

## License

By contributing, you agree that your contributions will be licensed under the MIT License.