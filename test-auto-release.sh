#!/bin/bash

# Test script for auto-release workflow
# This simulates what the GitHub Action will do

echo "=== Auto-Release Test Script ==="
echo ""

# Get current version
CURRENT_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
echo "Current version: $CURRENT_VERSION"

# Parse version components
IFS='.' read -ra VERSION_PARTS <<< "$CURRENT_VERSION"
MAJOR="${VERSION_PARTS[0]}"
MINOR="${VERSION_PARTS[1]}"
PATCH="${VERSION_PARTS[2]}"

# Simulate version bump
echo ""
echo "Version bump options:"
echo "1) Patch: $MAJOR.$MINOR.$((PATCH + 1))"
echo "2) Minor: $MAJOR.$((MINOR + 1)).0"
echo "3) Major: $((MAJOR + 1)).0.0"

# Check for changes
CHANGES=$(git status --porcelain | grep -E "^[AM].*\.(java|json|properties|gradle)$" | wc -l)
if [ "$CHANGES" -gt 0 ]; then
    echo ""
    echo "Detected $CHANGES file changes - would trigger auto-release"
fi

# Generate sample changelog
echo ""
echo "=== Sample Changelog ==="
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -z "$LAST_TAG" ]; then
    echo "No previous tags found - would include all commits"
    git log --pretty=format:"- %s (%an)" --no-merges | head -10
else
    echo "Changes since $LAST_TAG:"
    git log ${LAST_TAG}..HEAD --pretty=format:"- %s (%an)" --no-merges
fi

echo ""
echo ""
echo "=== To trigger auto-release: ==="
echo "1. Commit your changes:"
echo "   git add ."
echo "   git commit -m \"Your change description\""
echo ""
echo "2. Push to main or forge-1.20.1:"
echo "   git push origin main"
echo ""
echo "The GitHub Action will then:"
echo "- Build the mod"
echo "- Auto-increment version to $MAJOR.$MINOR.$((PATCH + 1))"
echo "- Create release with changelog"
echo "- Upload JAR as release asset"