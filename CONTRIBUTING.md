# Contributing to Musicya

Thank you for your interest in contributing to Musicya!

## Getting Started

1. **Fork** the repository on GitHub
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Musicya.git
   cd Musicya
   ```
3. **Create a branch** for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Set up the development environment**:
   - Android Studio Hedgehog or newer
   - JDK 17
   - Android SDK API 34

## Development

### Building

```bash
./gradlew assembleDebug     # Debug build
./gradlew assembleRelease   # Release build
./gradlew test              # Run unit tests
./gradlew lint              # Run lint checks
```

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused (single responsibility)

### Testing

- Write unit tests for ViewModels and utilities
- Use MockK for mocking
- Use Turbine for testing Flows
- Test happy paths and error cases

### Commit Messages

Write clear, descriptive commit messages. Do not use prefixes like `feat:`, `fix:`, etc. Just describe what was done:

```
Add fuzzy search with Levenshtein distance matching

Implement typo-tolerant search that finds songs even when the
query doesn't exactly match the song title or artist name.
```

## Pull Request Process

1. **Update documentation** if your changes affect the API or behavior
2. **Add tests** for new functionality
3. **Ensure all tests pass** locally before submitting
4. **Open a pull request** against the `dev/production-upgrades` branch
5. **Describe your changes** clearly in the PR description

## Code of Conduct

- Be respectful and constructive
- Focus on the code, not the person
- Welcome different perspectives and experiences

## Questions?

- Open an issue on GitHub for bugs or feature requests
- Check existing issues before creating duplicates

## License

By contributing to Musicya, you agree that your contributions will be licensed under the MIT License.