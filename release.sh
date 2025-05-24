#!/bin/bash

# Astro Expansion Release Script with Auto-Versioning
# This script helps create a new release with automatic version management

set -e

echo "🚀 Astro Expansion Release Script"
echo "================================"

# Check if we're in the right directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: Must be run from the project root directory"
    exit 1
fi

# Get current version
CURRENT_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
echo "Current version: $CURRENT_VERSION"

# Ask for version bump type
echo ""
echo "Select version bump type:"
echo "1) Patch (x.x.X) - Bug fixes, small changes"
echo "2) Minor (x.X.0) - New features, backwards compatible"
echo "3) Major (X.0.0) - Breaking changes, major updates"
echo "4) Manual - Enter version manually"
echo ""
read -p "Choice [1-4]: " CHOICE

case $CHOICE in
    1)
        VERSION_TYPE="patch"
        ./auto_version.sh patch
        ;;
    2)
        VERSION_TYPE="minor"
        ./auto_version.sh minor
        ;;
    3)
        VERSION_TYPE="major"
        ./auto_version.sh major
        ;;
    4)
        read -p "Enter new version (e.g., 1.0.1): " NEW_VERSION
        if [ -z "$NEW_VERSION" ]; then
            echo "❌ Error: Version cannot be empty"
            exit 1
        fi
        sed -i "s/mod_version=.*/mod_version=$NEW_VERSION/" gradle.properties
        sed -i "s/version=\".*\"/version=\"$NEW_VERSION\"/" src/main/resources/META-INF/mods.toml
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac

# Get the new version
NEW_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
echo "📝 New version: $NEW_VERSION"

# Clean and build
echo "🔨 Building mod..."
./gradlew clean build

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    # Restore original version
    sed -i "s/mod_version=.*/mod_version=$CURRENT_VERSION/" gradle.properties
    sed -i "s/version=\".*\"/version=\"$CURRENT_VERSION\"/" src/main/resources/META-INF/mods.toml
    exit 1
fi

# Get mod info
MC_VERSION=$(grep "minecraft_version=" gradle.properties | cut -d'=' -f2)
MOD_ID=$(grep "mod_id=" gradle.properties | cut -d'=' -f2)
JAR_NAME="${MOD_ID}-${NEW_VERSION}.jar"
RELEASE_NAME="${MOD_ID}-${NEW_VERSION}-mc${MC_VERSION}.jar"

# Create release directory
mkdir -p "releases/v${NEW_VERSION}"

# Copy and rename JAR
echo "📦 Preparing release JAR..."
cp "build/libs/$JAR_NAME" "releases/v${NEW_VERSION}/$RELEASE_NAME"

# Create release notes template
cat > "RELEASE_NOTES_v${NEW_VERSION}.md" << EOF
# Astro Expansion v${NEW_VERSION}

**Minecraft Version:** ${MC_VERSION}  
**Forge Version:** 47.2.0+
**Release Date:** $(date +%Y-%m-%d)

## 📝 Changelog

### Added
- 

### Changed
- 

### Fixed
- 

## 📦 Installation
1. Install Minecraft Forge for ${MC_VERSION} (version 47.2.0 or higher)
2. Download the JAR file: \`${RELEASE_NAME}\`
3. Place it in your \`.minecraft/mods\` folder
4. Launch Minecraft and enjoy!

## 🔧 Getting Started
\`\`\`
/give @p astroexpansion:titanium_ore 64
/give @p astroexpansion:basic_generator
/give @p astroexpansion:material_processor
\`\`\`

## 📚 Documentation
- [Quick Start Guide](guides/QUICK_START.md)
- [Storage System Guide](guides/STORAGE_SYSTEM.md)
- [Space Progression Guide](guides/SPACE_PROGRESSION.md)
- [All Guides](GUIDES.md)

---

**Full documentation:** [GitHub Repository](https://github.com/AstroLabs-AI/AstroExpansion)
EOF

echo "✅ Release preparation complete!"
echo ""
echo "📋 Next steps:"
echo "1. Edit RELEASE_NOTES_v${NEW_VERSION}.md with actual changes"
echo "2. Commit changes: git add . && git commit -m \"Release v${NEW_VERSION}\""
echo "3. Push: git push"
echo ""
echo "📦 Release JAR: releases/v${NEW_VERSION}/$RELEASE_NAME"
echo ""
echo "The GitHub Action will automatically create a release when it detects the new version!"