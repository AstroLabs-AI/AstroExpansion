#!/bin/bash

# Astro Expansion Release Script
# Usage: ./create_release.sh <version> <phase_name>
# Example: ./create_release.sh 0.3.0 "Space Exploration"

VERSION=$1
PHASE=$2

if [ -z "$VERSION" ] || [ -z "$PHASE" ]; then
    echo "Usage: ./create_release.sh <version> <phase_name>"
    echo "Example: ./create_release.sh 0.3.0 \"Space Exploration\""
    exit 1
fi

echo "Creating release for Astro Expansion v$VERSION - $PHASE"

# Clean and build
echo "Building mod..."
./gradlew clean build

# Check if build succeeded
if [ ! -f "build/libs/astroexpansion-1.0.0.jar" ]; then
    echo "Build failed! No JAR file found."
    exit 1
fi

# Create release directory
RELEASE_DIR="releases/v$VERSION"
mkdir -p $RELEASE_DIR

# Copy JAR with version name
cp build/libs/astroexpansion-1.0.0.jar "$RELEASE_DIR/astroexpansion-$VERSION-mc1.20.1.jar"

# Create release info
cat > "$RELEASE_DIR/release_info.txt" << EOF
Astro Expansion v$VERSION - $PHASE
Built on: $(date)
Minecraft: 1.20.1
Forge: 47.2.0+
EOF

echo "Release files created in $RELEASE_DIR/"
echo ""
echo "Next steps:"
echo "1. Create git tag: git tag -a v$VERSION -m \"Release v$VERSION: $PHASE\""
echo "2. Push tag: git push origin v$VERSION"
echo "3. Create GitHub release with the JAR from $RELEASE_DIR/"
echo "4. Upload release notes from RELEASE_NOTES_v$VERSION.md"