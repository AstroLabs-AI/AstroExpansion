#!/bin/bash

# Astro Expansion Release Script
# This script helps create a new release with proper versioning

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

# Ask for new version
read -p "Enter new version (e.g., 1.0.1): " NEW_VERSION

if [ -z "$NEW_VERSION" ]; then
    echo "❌ Error: Version cannot be empty"
    exit 1
fi

# Update version in gradle.properties
echo "📝 Updating version to $NEW_VERSION..."
sed -i.bak "s/mod_version=.*/mod_version=$NEW_VERSION/" gradle.properties
rm gradle.properties.bak

# Clean and build
echo "🔨 Building mod..."
./gradlew clean build

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    # Restore original version
    sed -i.bak "s/mod_version=.*/mod_version=$CURRENT_VERSION/" gradle.properties
    rm gradle.properties.bak
    exit 1
fi

# Get mod info
MC_VERSION=$(grep "minecraft_version=" gradle.properties | cut -d'=' -f2)
MOD_ID=$(grep "mod_id=" gradle.properties | cut -d'=' -f2)
JAR_NAME="${MOD_ID}-${NEW_VERSION}.jar"
RELEASE_NAME="${MOD_ID}-${NEW_VERSION}-mc${MC_VERSION}.jar"

# Copy and rename JAR
echo "📦 Preparing release JAR..."
mkdir -p releases
cp "build/libs/$JAR_NAME" "releases/$RELEASE_NAME"

# Create release notes template
cat > releases/release_notes_${NEW_VERSION}.md << EOF
# Astro Expansion v${NEW_VERSION}

**Minecraft Version:** ${MC_VERSION}  
**Forge Version:** 47.2.0+

## 📝 Changelog

### Added
- 

### Changed
- 

### Fixed
- 

## 📦 Installation
1. Install Minecraft Forge for ${MC_VERSION} (version 47.2.0 or higher)
2. Download the JAR file
3. Place it in your \`.minecraft/mods\` folder
4. Launch Minecraft and enjoy!

## 🔧 Getting Started
\`\`\`
/give @p astroexpansion:titanium_ore 64
/give @p astroexpansion:basic_generator
/give @p astroexpansion:material_processor
\`\`\`

---

**Full documentation:** [GitHub Repository](https://github.com/AstroExpansion/AstroExpansion)
EOF

echo "✅ Release preparation complete!"
echo ""
echo "📋 Next steps:"
echo "1. Edit releases/release_notes_${NEW_VERSION}.md with actual changes"
echo "2. Commit changes: git add . && git commit -m \"Release v${NEW_VERSION}\""
echo "3. Create tag: git tag v${NEW_VERSION}"
echo "4. Push: git push && git push --tags"
echo ""
echo "📦 Release JAR: releases/$RELEASE_NAME"
echo ""
echo "The GitHub Action will automatically create a release when you push the tag!"