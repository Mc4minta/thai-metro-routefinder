#!/bin/bash
# RouteFinder Build Script - Automates the creation of Windows installer
# This script should be run from the project root directory

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INSTALLER_DIR="$PROJECT_ROOT/installer"
BUILD_TOOLS_DIR="$PROJECT_ROOT/build-tools"
TARGET_DIR="$PROJECT_ROOT/target"

echo "=========================================="
echo "RouteFinder Build Automation Script"
echo "=========================================="
echo ""

# Step 1: Build fat JAR
echo "Step 1: Building fat JAR with Maven..."
cd "$PROJECT_ROOT"
mvn clean package -DskipTests

if [ ! -f "$TARGET_DIR/routefinder-fat.jar" ]; then
    echo "ERROR: Fat JAR not created"
    exit 1
fi
echo "✓ Fat JAR created successfully"
echo ""

# Step 2: Copy JAR to installer directory
echo "Step 2: Preparing installer directory..."
mkdir -p "$INSTALLER_DIR"
cp "$TARGET_DIR/routefinder-fat.jar" "$INSTALLER_DIR/"
echo "✓ JAR copied to installer directory"
echo ""

# Step 3: Summary
echo "=========================================="
echo "Build Complete!"
echo "=========================================="
echo ""
echo "Installer files location:"
echo "  $INSTALLER_DIR"
echo ""
echo "Installation methods:"
echo "  1. Windows Batch Installer (recommended):"
echo "     - Run: Install-RouteFinder.bat"
echo ""
echo "  2. PowerShell Installer:"
echo "     - Run: powershell -ExecutionPolicy Bypass -File RouteFinder-Installer.ps1"
echo ""
echo "  3. Manual Installation:"
echo "     - Copy the routefinder-fat.jar and jre folder to desired location"
echo "     - Run: RouteFinder.bat"
echo ""
echo "Note: The installer automatically bundles the JRE (Java Runtime Environment)"
echo "      Users do not need to install Java separately."
echo ""
