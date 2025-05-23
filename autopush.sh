#!/bin/bash
# Script to manage auto-push functionality

case "$1" in
    enable)
        echo "Enabling auto-push..."
        cp .git/hooks/post-commit .git/hooks/post-commit.backup 2>/dev/null
        cat > .git/hooks/post-commit << 'EOF'
#!/bin/bash
# Auto-push hook for major changes

# Get the current branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Check if we're on a feature branch
if [[ "$BRANCH" == "forge-1.20.1" ]] || [[ "$BRANCH" == "main" ]]; then
    echo "Auto-pushing to origin/$BRANCH..."
    git push origin "$BRANCH" --tags
    
    if [ $? -eq 0 ]; then
        echo "✅ Successfully pushed to origin/$BRANCH"
    else
        echo "❌ Failed to push to origin/$BRANCH"
        echo "You can manually push later with: git push origin $BRANCH"
    fi
else
    echo "ℹ️ Not auto-pushing from branch: $BRANCH"
    echo "Auto-push only enabled for 'forge-1.20.1' and 'main' branches"
fi
EOF
        chmod +x .git/hooks/post-commit
        echo "✅ Auto-push enabled!"
        ;;
    disable)
        echo "Disabling auto-push..."
        rm -f .git/hooks/post-commit
        if [ -f .git/hooks/post-commit.backup ]; then
            mv .git/hooks/post-commit.backup .git/hooks/post-commit
        fi
        echo "✅ Auto-push disabled!"
        ;;
    status)
        if [ -f .git/hooks/post-commit ]; then
            echo "✅ Auto-push is ENABLED"
            echo "Current branch: $(git rev-parse --abbrev-ref HEAD)"
        else
            echo "❌ Auto-push is DISABLED"
        fi
        ;;
    *)
        echo "Usage: $0 {enable|disable|status}"
        echo ""
        echo "  enable  - Enable automatic push after commits"
        echo "  disable - Disable automatic push after commits"
        echo "  status  - Check if auto-push is enabled"
        exit 1
        ;;
esac