#!/bin/bash
# Auto-versioning script for AstroExpansion

# Get current version from gradle.properties
CURRENT_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
echo "Current version: $CURRENT_VERSION"

# Parse version components
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Function to increment version
increment_version() {
    local type=$1
    
    case $type in
        major)
            MAJOR=$((MAJOR + 1))
            MINOR=0
            PATCH=0
            ;;
        minor)
            MINOR=$((MINOR + 1))
            PATCH=0
            ;;
        patch)
            PATCH=$((PATCH + 1))
            ;;
        *)
            echo "Usage: $0 [major|minor|patch]"
            exit 1
            ;;
    esac
    
    NEW_VERSION="$MAJOR.$MINOR.$PATCH"
}

# Get version type from argument or default to patch
VERSION_TYPE=${1:-patch}
increment_version $VERSION_TYPE

echo "New version: $NEW_VERSION"

# Update gradle.properties
sed -i "s/mod_version=.*/mod_version=$NEW_VERSION/" gradle.properties

# Update mods.toml
sed -i "s/version=\".*\"/version=\"$NEW_VERSION\"/" src/main/resources/META-INF/mods.toml

echo "Version updated to $NEW_VERSION in gradle.properties and mods.toml"
echo "NEW_VERSION=$NEW_VERSION" > version.env