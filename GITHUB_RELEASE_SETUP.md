# GitHub Release Setup Guide

## 🚀 Automated Build & Release System

This repository is now configured with GitHub Actions for automated building and releasing of Astro Expansion.

## 📋 Setup Complete

### 1. **GitHub Actions Workflows**

#### Build Workflow (`.github/workflows/build.yml`)
- Triggers on: Push to main branches, Pull requests
- Actions:
  - Sets up Java 17
  - Builds the mod with Gradle
  - Uploads build artifacts
  - Uploads build reports on failure

#### Release Workflow (`.github/workflows/release.yml`)
- Triggers on: Git tags starting with 'v' (e.g., v1.0.0)
- Also supports manual trigger with version input
- Actions:
  - Builds the mod with the tagged version
  - Creates a GitHub Release
  - Attaches the built JAR file
  - Generates release notes automatically
  - Placeholders for CurseForge/Modrinth upload

#### Publish Workflow (`.github/workflows/publish.yml`)
- Triggers on: GitHub release published
- Ready for CurseForge and Modrinth integration
- Currently contains commented placeholders

### 2. **Additional Setup Files**

- **README.md**: Project description with badges
- **LICENSE**: MIT License
- **CONTRIBUTING.md**: Contribution guidelines
- **.github/dependabot.yml**: Automated dependency updates
- **release.sh**: Manual release helper script

## 🔧 How to Use

### Automatic Release (Recommended)

1. **Update version** in `gradle.properties`:
   ```properties
   mod_version=1.0.1
   ```

2. **Commit your changes**:
   ```bash
   git add .
   git commit -m "Your changes"
   ```

3. **Create and push a tag**:
   ```bash
   git tag v1.0.1
   git push origin forge-1.20.1
   git push origin v1.0.1
   ```

4. **GitHub Actions will automatically**:
   - Build the mod
   - Create a release named "Astro Expansion v1.0.1"
   - Attach the JAR file as `astroexpansion-1.0.1-mc1.20.1.jar`
   - Generate release notes

### Manual Release

Use the provided script:
```bash
./release.sh
```

This will:
- Prompt for new version
- Update gradle.properties
- Build the mod
- Create release files
- Provide instructions for git commands

## 🔑 Required Secrets (Future)

To enable full platform publishing, add these secrets to your GitHub repository:

1. **CurseForge**: `CURSEFORGE_API_TOKEN`
2. **Modrinth**: `MODRINTH_TOKEN`

Get these from:
- CurseForge: https://authors.curseforge.com/account/api-tokens
- Modrinth: https://modrinth.com/settings/pats

## 📊 Build Status

After pushing to GitHub, you'll see:
- Build status badge in README
- Actions tab showing workflow runs
- Releases page with downloadable JARs

## 🚦 First Release Ready!

The repository has been tagged with `v1.0.0` and is ready to be pushed. Once you:

1. Create a GitHub repository
2. Add it as remote: `git remote add origin YOUR_REPO_URL`
3. Push: `git push -u origin forge-1.20.1 --tags`

GitHub Actions will automatically create your first release!

## 📝 Release Notes Template

Each release will include:
- Minecraft & Forge versions
- Installation instructions
- Feature highlights
- Getting started commands
- Link to full documentation

## 🎯 Next Steps

1. Push this repository to GitHub
2. Watch the Actions tab for build status
3. Check Releases page for the automated release
4. Share your mod with the community!

---

**Note**: The mod JAR will be named following the pattern:
`astroexpansion-VERSION-mcMC_VERSION.jar`

Example: `astroexpansion-1.0.0-mc1.20.1.jar`