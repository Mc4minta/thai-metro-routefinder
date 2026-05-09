# RouteFinder Releases

This directory contains distribution packages for RouteFinder releases.

## Release Packages

Each release includes:
- **RouteFinder-X.X.X.zip** - Complete installation package

## Installation

### For End Users

1. **Download** the latest `RouteFinder-X.X.X.zip` file
2. **Extract** the ZIP file to a location of your choice
3. **Open** the extracted `RouteFinder` folder
4. **Double-click** `Install-RouteFinder.bat`
5. **Follow** the installer prompts
6. Application will be installed to `C:\Program Files\RouteFinder`

### System Requirements

- Windows 7 or later
- 500 MB free disk space (for installation)
- Internet connection (recommended, for downloading)

## Uninstallation

1. Open **Control Panel** → **Programs** → **Programs and Features**
2. Find **RouteFinder** in the list
3. Click **Uninstall** and follow the prompts

Or manually:
- Delete: `C:\Program Files\RouteFinder`
- Delete shortcuts from Start Menu and Desktop

## Building Releases

For developers who want to create release packages:

```powershell
# Build with default version (1.0.0)
.\build-release-zip.ps1

# Build with specific version
.\build-release-zip.ps1 -Version "1.0.1"
```

This creates a ZIP file in the `releases/` directory ready for distribution.

## Release History

- **1.0.0** - Initial release
