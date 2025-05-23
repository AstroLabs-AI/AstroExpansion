# Contributing to Astro Expansion

Thank you for your interest in contributing to Astro Expansion! We welcome contributions from the community.

## 🚀 Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/AstroExpansion.git
   cd AstroExpansion
   ```
3. **Add the upstream remote**:
   ```bash
   git remote add upstream https://github.com/AstroExpansion/AstroExpansion.git
   ```

## 🔧 Development Setup

1. **Install Java 17** or newer
2. **Set up your IDE**:
   - IntelliJ IDEA: Import as Gradle project
   - Eclipse: Run `./gradlew eclipse` then import
3. **Build the project**:
   ```bash
   ./gradlew build
   ```

## 📝 Making Changes

1. **Create a new branch** for your feature:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Follow the code style**:
   - Use 4 spaces for indentation (no tabs)
   - Follow Java naming conventions
   - Add JavaDoc for public methods
   - Keep methods concise and focused

3. **Test your changes**:
   - Run `./gradlew build` to ensure compilation
   - Test in-game with a Minecraft client
   - Add unit tests for new functionality

4. **Commit your changes**:
   ```bash
   git add .
   git commit -m "Add feature: brief description"
   ```

## 🎯 Pull Request Guidelines

1. **Update your fork**:
   ```bash
   git fetch upstream
   git rebase upstream/forge-1.20.1
   ```

2. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

3. **Create a Pull Request**:
   - Use a clear, descriptive title
   - Reference any related issues
   - Describe what changes you made and why
   - Include screenshots for visual changes

### PR Checklist
- [ ] Code compiles without warnings
- [ ] Tested in-game
- [ ] Added/updated documentation
- [ ] Followed code style guidelines
- [ ] All tests pass

## 🐛 Reporting Issues

1. **Search existing issues** to avoid duplicates
2. **Use issue templates** when available
3. **Provide details**:
   - Minecraft version
   - Forge version
   - Mod version
   - Steps to reproduce
   - Expected vs actual behavior
   - Crash logs (use pastebin)

## 💡 Feature Requests

1. **Check the roadmap** in README.md
2. **Search existing requests**
3. **Be specific** about the feature
4. **Explain the use case**

## 🏗️ Project Structure

```
src/main/java/com/astrolabs/astroexpansion/
├── common/          # Shared code (blocks, items, etc.)
│   ├── blocks/      # Block classes
│   ├── blockentities/ # Tile entities
│   ├── items/       # Item classes
│   ├── recipes/     # Recipe types
│   └── registry/    # Object registration
├── client/          # Client-only code
│   ├── gui/         # GUI screens
│   └── renderer/    # Block/entity renderers
├── datagen/         # Data generation
└── api/             # Public API
```

## 📚 Code Guidelines

### Naming Conventions
- **Classes**: `PascalCase`
- **Methods**: `camelCase`
- **Constants**: `UPPER_SNAKE_CASE`
- **Packages**: `lowercase`

### Energy System
- Always use Forge Energy (FE)
- Standard rates: 1 FE = 1 RF
- Respect capability sides

### Recipes
- Use data generation when possible
- Follow vanilla recipe patterns
- Document custom recipe types

## 🎮 Testing

### Local Testing
1. Run client: `./gradlew runClient`
2. Run server: `./gradlew runServer`
3. Run data generation: `./gradlew runData`

### Test Coverage
- Critical systems need unit tests
- GUI code needs in-game testing
- Document test scenarios

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

## 💬 Communication

- **GitHub Issues**: Bug reports and features
- **Pull Requests**: Code contributions
- **Discussions**: General questions and ideas

Thank you for contributing to Astro Expansion! 🚀