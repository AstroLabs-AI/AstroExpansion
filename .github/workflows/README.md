# GitHub Actions Workflows

This directory contains automated workflows for the AstroExpansion project.

## Workflows

### 1. Build and Release (`build.yml`)
**Triggers:**
- Push to `forge-1.20.1` or `main` branches
- Pull requests to these branches
- Tag pushes matching `v*`

**Actions:**
- Builds the mod with Java 17
- Uploads build artifacts
- Automatically creates releases when tags are pushed
- Attaches JAR files to releases

### 2. Create Release (`release.yml`)
**Triggers:**
- Manual workflow dispatch (Actions tab → Create Release → Run workflow)

**Inputs:**
- `version`: Version number (e.g., 1.0.1)
- `release_notes`: Optional release notes

**Actions:**
- Updates version in gradle.properties
- Builds the mod
- Creates and pushes git tag
- Creates GitHub release with JAR attachment

## Usage

### Automatic Releases (Recommended)

1. **Create a new release via Actions:**
   ```
   Go to Actions → Create Release → Run workflow
   Enter version: 1.0.1
   Enter release notes: "Fixed duplicate registration bug"
   Click "Run workflow"
   ```

2. **Create a release by pushing a tag:**
   ```bash
   git tag -a v1.0.1 -m "Version 1.0.1"
   git push origin v1.0.1
   ```

### Manual Release Process

If you prefer to create releases manually:

1. Update version in `gradle.properties`
2. Build the mod: `./gradlew clean build`
3. Create and push tag: `git tag -a v1.0.1 -m "Version 1.0.1"`
4. Push tag: `git push origin v1.0.1`
5. GitHub Actions will automatically create the release

## Auto-Push Feature

The repository includes an auto-push feature that automatically pushes commits to GitHub.

### Enable Auto-Push
```bash
./autopush.sh enable
```

### Disable Auto-Push
```bash
./autopush.sh disable
```

### Check Status
```bash
./autopush.sh status
```

**Note:** Auto-push only works on `forge-1.20.1` and `main` branches.

## Secrets Required

No additional secrets are required. The workflows use the default `GITHUB_TOKEN` which is automatically provided.

## Troubleshooting

### Build Failures
- Check the Actions tab for detailed logs
- Ensure Java 17 compatibility
- Verify gradle wrapper is executable

### Release Creation Failures
- Ensure you have push permissions
- Check that the version tag doesn't already exist
- Verify the JAR file is being created in `build/libs/`

### Auto-Push Not Working
- Ensure the git hook is executable: `chmod +x .git/hooks/post-commit`
- Check you're on an enabled branch: `forge-1.20.1` or `main`
- Verify you have push permissions to the repository