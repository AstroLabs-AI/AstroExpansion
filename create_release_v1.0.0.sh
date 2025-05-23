#!/bin/bash

# AstroExpansion v1.0.0 Release Script

echo "Creating GitHub release for AstroExpansion v1.0.0..."

# Create git tag if it doesn't exist
if ! git rev-parse v1.0.0 >/dev/null 2>&1; then
    echo "Creating tag v1.0.0..."
    git tag -a v1.0.0 -m "Version 1.0.0 - The Final Frontier Update"
    git push origin v1.0.0
else
    echo "Tag v1.0.0 already exists"
fi

# Create release using GitHub CLI
echo "Creating GitHub release..."
gh release create v1.0.0 \
    ./releases/v1.0.0/astroexpansion-1.0.0-mc1.20.1.jar \
    --title "AstroExpansion v1.0.0 - The Final Frontier 🚀" \
    --notes-file RELEASE_NOTES_v1.0.0.md \
    --target forge-1.20.1

echo "Release created successfully!"
echo ""
echo "You can also create the release manually at:"
echo "https://github.com/AstroLabs-AI/AstroExpansion/releases/new"
echo ""
echo "Upload file: releases/v1.0.0/astroexpansion-1.0.0-mc1.20.1.jar"