# RouteFinder Release - Quick Start

## For End Users

### Installation (3 Easy Steps)

1. **Download** `RouteFinder-1.0.0.zip` from the releases page
2. **Extract** the ZIP file to your desired location
3. **Run** `Install-RouteFinder.bat` by double-clicking it

The installer will:
- Install RouteFinder to `C:\Program Files\RouteFinder`
- Create shortcuts on your Desktop and Start Menu
- Set up uninstall in Control Panel

### System Requirements

- **OS**: Windows 7 or later
- **Memory**: 1 GB minimum (2 GB recommended)
- **Disk Space**: 500 MB for installation
- **Java**: Included in the package (no need to install separately)

### Running RouteFinder

After installation, you can launch RouteFinder by:
- Clicking the Desktop shortcut, or
- Finding "RouteFinder" in the Start Menu, or
- Running `RouteFinder.bat` from the installation folder

### Uninstalling

1. Open **Control Panel** → **Programs** → **Programs and Features**
2. Find **RouteFinder** in the list
3. Click **Uninstall** and follow the prompts

---

## For Developers / Release Managers

### Building a Release

To create a release package:

```powershell
# Navigate to the RouteFinder directory
cd path\to\routefinder

# Ensure the installer is built
.\build.bat

# Create the release ZIP
.\build-release-zip.ps1 -Version "1.0.0"
```

This creates `releases/RouteFinder-1.0.0.zip` (~40 MB).

### Publishing a Release

1. Create a GitHub release with tag `v1.0.0`
2. Upload the ZIP file as a release asset
3. Add release notes with:
   - New features
   - Bug fixes
   - Installation instructions (link to this file)

### Testing a Release

Before publishing:

```powershell
# Extract to a test directory
$zip = "releases/RouteFinder-1.0.0.zip"
Expand-Archive -Path $zip -DestinationPath "$env:TEMP\RouteFinder-Test"

# Test the installer
& "$env:TEMP\RouteFinder-Test\RouteFinder\Install-RouteFinder.bat"
```

---

## Troubleshooting

### "Windows protected your PC" Warning

This is normal for unsigned installers. Click **More info** → **Run anyway**.

To eliminate this warning (future enhancement):
- Code-sign the batch/PowerShell scripts
- Use a `.exe` installer via NSIS

### Installation Fails

1. **Run as Administrator**: Right-click `Install-RouteFinder.bat` → **Run as administrator**
2. **Check Disk Space**: Ensure 500+ MB free space
3. **Check Permissions**: Ensure you have write access to `C:\Program Files`

### Application Won't Start

1. Verify Java is working: Open `cmd` and run:
   ```cmd
   C:\Program Files\RouteFinder\jre\bin\java.exe -version
   ```
2. Check for port conflicts if applicable
3. Review logs in the application directory

---

## Release History

- **1.0.0** (2026-05-09) - Initial ZIP release
  - Size: 42.28 MB
  - Features: Basic installation and launcher
