#Requires -Version 5.0
<#
.SYNOPSIS
    Builds a distributable ZIP release package for RouteFinder
.DESCRIPTION
    Compresses the installer directory into a ZIP file with version number.
    Creates releases/ directory if it doesn't exist.
.PARAMETER Version
    Release version (e.g., "1.0.0"). Defaults to 1.0.0
.EXAMPLE
    .\build-release-zip.ps1
    .\build-release-zip.ps1 -Version "1.0.1"
#>

param(
    [string]$Version = "1.0.0"
)

# Configuration
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$installerDir = Join-Path $scriptRoot "installer"
$releasesDir = Join-Path $scriptRoot "releases"
$releaseZipName = "RouteFinder-$Version.zip"
$releaseZipPath = Join-Path $releasesDir $releaseZipName
$tempDir = Join-Path $env:TEMP "RouteFinder-Release-Temp"

Write-Host "╔════════════════════════════════════════════╗"
Write-Host "║  RouteFinder ZIP Release Builder            ║"
Write-Host "╚════════════════════════════════════════════╝"
Write-Host ""
Write-Host "Version: $Version"
Write-Host "Release: $releaseZipPath"
Write-Host ""

# Validate installer directory exists
if (-not (Test-Path $installerDir)) {
    Write-Host "❌ ERROR: Installer directory not found at: $installerDir" -ForegroundColor Red
    Write-Host "   Run 'build.bat' first to generate installer files." -ForegroundColor Yellow
    exit 1
}

# Check for required files
$requiredFiles = @(
    "Install-RouteFinder.bat",
    "RouteFinder.bat",
    "RouteFinder-Installer.ps1",
    "routefinder-fat.jar",
    "jre"
)

Write-Host "Checking required files..." -ForegroundColor Cyan
$allFilesPresent = $true
foreach ($file in $requiredFiles) {
    $filePath = Join-Path $installerDir $file
    if (Test-Path $filePath) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file (MISSING)" -ForegroundColor Red
        $allFilesPresent = $false
    }
}

if (-not $allFilesPresent) {
    Write-Host ""
    Write-Host "❌ ERROR: Some required files are missing." -ForegroundColor Red
    Write-Host "   Verify installer directory is complete." -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Create releases directory if it doesn't exist
if (-not (Test-Path $releasesDir)) {
    Write-Host "Creating releases directory..." -ForegroundColor Cyan
    New-Item -ItemType Directory -Path $releasesDir | Out-Null
    Write-Host "  ✓ Created: $releasesDir" -ForegroundColor Green
    Write-Host ""
}

# Clean up old temp directory if it exists
if (Test-Path $tempDir) {
    Remove-Item -Path $tempDir -Recurse -Force | Out-Null
}

# Create temporary staging directory
Write-Host "Staging files for compression..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path $tempDir | Out-Null
$stagingDir = Join-Path $tempDir "RouteFinder"

# Copy installer, excluding large temp/build artifacts
$excludePatterns = @("*/temp", "*/downloads", "*/.git", "*/docs", "*/man")
Write-Host "  Copying files (excluding temp directories)..." -ForegroundColor Gray

# Use robocopy for faster, more efficient copying with exclusions
$robocopyArgs = @(
    $installerDir,
    $stagingDir,
    "/E",  # Copy subdirectories including empty ones
    "/XD", "temp", "downloads", ".git",  # Exclude directories
    "/NP",  # No progress percentage
    "/NFL", "/NDL",  # No file/directory list
    "/R:0", "/W:0"  # No retry on failure
)

$robocopyOutput = robocopy @robocopyArgs 2>&1
if ($LASTEXITCODE -gt 8) {
    Write-Host "  ✗ Copy failed with robocopy exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}

Write-Host "  ✓ Files staged to temp directory" -ForegroundColor Green
Write-Host ""

# Remove any existing release file
if (Test-Path $releaseZipPath) {
    Write-Host "Removing existing release file..." -ForegroundColor Cyan
    Remove-Item -Path $releaseZipPath -Force
    Write-Host "  ✓ Removed: $releaseZipName" -ForegroundColor Green
    Write-Host ""
}

# Compress to ZIP
Write-Host "Compressing to ZIP (this may take 1-2 minutes)..." -ForegroundColor Cyan
try {
    # Get size before compression
    $sourceSize = (Get-ChildItem -Path $stagingDir -Recurse | Measure-Object -Property Length -Sum).Sum
    $sourceSizeMB = [math]::Round($sourceSize / 1MB, 2)
    
    # Compress
    Compress-Archive -Path $stagingDir -DestinationPath $releaseZipPath -CompressionLevel Optimal
    
    # Get size after compression
    $zipSize = (Get-Item $releaseZipPath).Length
    $zipSizeMB = [math]::Round($zipSize / 1MB, 2)
    $compression = [math]::Round((1 - ($zipSize / $sourceSize)) * 100, 1)
    
    Write-Host "  ✓ Compression complete" -ForegroundColor Green
    Write-Host "    Source size: $sourceSizeMB MB" -ForegroundColor Gray
    Write-Host "    ZIP size: $zipSizeMB MB" -ForegroundColor Gray
    Write-Host "    Compression ratio: $compression%" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "  ✗ Compression failed: $_" -ForegroundColor Red
    exit 1
}

# Clean up temp directory
Remove-Item -Path $tempDir -Recurse -Force | Out-Null

# Verify ZIP was created
if (Test-Path $releaseZipPath) {
    Write-Host "✅ SUCCESS: Release package created!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Release file: $releaseZipName" -ForegroundColor Cyan
    Write-Host "Location: $releaseZipPath" -ForegroundColor Cyan
    Write-Host "Size: $zipSizeMB MB" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "📋 Next steps:" -ForegroundColor Yellow
    Write-Host "  1. Test extraction and installation"
    Write-Host "  2. Upload to GitHub releases or file hosting"
    Write-Host "  3. Share download link with users"
    Write-Host ""
    Write-Host "📖 Installation instructions for users:" -ForegroundColor Yellow
    Write-Host "  1. Download: $releaseZipName" -ForegroundColor Gray
    Write-Host "  2. Extract the ZIP file" -ForegroundColor Gray
    Write-Host "  3. Open extracted RouteFinder folder" -ForegroundColor Gray
    Write-Host "  4. Double-click 'Install-RouteFinder.bat'" -ForegroundColor Gray
    Write-Host "  5. Follow the installer prompts" -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host "❌ ERROR: ZIP file was not created." -ForegroundColor Red
    exit 1
}
