# Windows Installer Build Guide for RouteFinder

This guide explains how to build and distribute RouteFinder as a Windows installer.

## Quick Start

### For End Users (Installation)

1. **Using the Batch Installer (Recommended)**
   ```cmd
   Install-RouteFinder.bat
   ```
   - Double-click to run
   - Follow the on-screen prompts
   - Application will be installed to `C:\Program Files\RouteFinder`
   - Shortcuts will be created in Start Menu and Desktop

2. **Using PowerShell Installer**
   ```powershell
   powershell -ExecutionPolicy Bypass -File RouteFinder-Installer.ps1
   ```

3. **Manual Installation**
   - Extract all files to desired directory
   - Run `RouteFinder.bat` to launch the application

### For Developers (Building the Installer)

#### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6+
- Windows Operating System
- (Optional) PowerShell 5.0+ for advanced features

#### Build Steps

1. **Clone or enter the build branch**
   ```powershell
   git checkout build
   ```

2. **Run the build script**
   ```powershell
   .\build.bat
   ```
   
   Or manually:
   ```powershell
   mvn clean package -DskipTests
   ```

3. **Output**
   - `target/routefinder-fat.jar` - The bundled application JAR
   - `installer/` directory containing installation files

#### Installer Contents

- **routefinder-fat.jar** - The complete application with all dependencies
- **RouteFinder.bat** - Launcher script for the application
- **jre/** - Portable Java Runtime Environment (JRE 11)
- **RouteFinder-Installer.ps1** - PowerShell-based installer
- **Install-RouteFinder.bat** - Batch wrapper for easy installation
- **Uninstall.ps1** - Uninstaller script (created during installation)

## Architecture

### Application Deployment

```
C:\Program Files\RouteFinder\
├── routefinder-fat.jar          (Main application JAR)
├── RouteFinder.bat              (Launcher script)
├── jre/                         (Portable JRE)
│   ├── bin/
│   │   └── java.exe            (Java executable)
│   ├── lib/
│   └── ...
└── Uninstall.ps1               (Uninstaller)
```

### How RouteFinder Launches

1. User clicks `RouteFinder.bat` or Start Menu shortcut
2. Batch script locates bundled JRE at `jre\bin\java.exe`
3. Runs: `java -jar routefinder-fat.jar`
4. Application starts with no external dependencies

## File Descriptions

### build.bat
Windows batch script that:
- Compiles the project with Maven
- Creates the fat JAR
- Prepares the installer directory

### build.sh
Bash script (for Linux/macOS) with the same functionality as build.bat

### pom.xml (Modified)
Updated with:
- Maven Shade Plugin: Creates fat JAR with all dependencies
- Maven Assembly Plugin: Additional packaging options
- Java 11 compiler configuration

### RouteFinder.bat
Launcher script that:
- Detects bundled JRE
- Launches the application with proper Java settings
- Handles errors gracefully

### RouteFinder-Installer.ps1
PowerShell installer that:
- Creates installation directory
- Copies files (JAR, JRE, launcher)
- Creates Start Menu shortcuts
- Creates Desktop shortcut
- Sets up uninstaller

### Install-RouteFinder.bat
Batch wrapper that:
- Calls the PowerShell installer
- Handles execution policy
- Provides user feedback

## Distribution

### Package for Distribution

To create a distribution package:

1. Build the installer:
   ```powershell
   .\build.bat
   ```

2. Copy installer files:
   ```powershell
   # Copy installer directory to distribution location
   Copy-Item -Path "installer" -Destination "RouteFinder-1.0.0" -Recurse
   ```

3. Create distribution archive:
   ```powershell
   Compress-Archive -Path "RouteFinder-1.0.0" -DestinationPath "RouteFinder-1.0.0.zip"
   ```

4. Share the ZIP file or files

### Alternative: Windows Installer Package

To create a traditional `.exe` installer, you can use NSIS or Inno Setup:

#### Using NSIS (if small installer desired)

Create `installer.nsi`:
```nsis
OutFile "RouteFinder-Setup.exe"
InstallDir "$PROGRAMFILES\RouteFinder"
Section "Install"
    SetOutPath "$INSTDIR"
    File "routefinder-fat.jar"
    File "RouteFinder.bat"
    SetOutPath "$INSTDIR\jre"
    File /r "jre\*.*"
SectionEnd
```

Compile:
```powershell
& "C:\Program Files (x86)\NSIS\makensis.exe" installer.nsi
```

## Uninstallation

### For End Users

1. **Via Control Panel (if installed via batch installer)**
   - Open Control Panel > Programs > Programs and Features
   - Find "RouteFinder" and click Uninstall

2. **Manual Uninstall**
   - Delete shortcuts from:
     - Start Menu: `%APPDATA%\Microsoft\Windows\Start Menu\Programs\RouteFinder`
     - Desktop: `%USERPROFILE%\Desktop\RouteFinder.lnk`
   - Delete installation directory: `C:\Program Files\RouteFinder`

### For Developers

To clean up build artifacts:
```powershell
# Remove build outputs
mvn clean

# Remove installer artifacts
Remove-Item -Path "installer\*" -Exclude "*.ps1", "*.bat", "*.nsi" -Force
```

## Troubleshooting

### Application won't start

1. **Check Java availability**
   - Verify `jre\bin\java.exe` exists
   - Try running manually: `jre\bin\java.exe -version`

2. **Check file permissions**
   - Ensure all files are readable
   - Run installer as Administrator if needed

3. **Check ports**
   - Ensure no other instance of RouteFinder is running
   - Check for port conflicts if applicable

### Installer fails

1. **Insufficient disk space**
   - Ensure at least 500 MB free space

2. **Permissions issues**
   - Run batch installer as Administrator
   - Run PowerShell installer with admin privileges

3. **Corrupt installation files**
   - Re-download or rebuild the installer

## Technical Details

### Fat JAR Creation

The Maven Shade Plugin:
- Merges all dependencies into one JAR
- Maintains the main class declaration
- Allows running with: `java -jar routefinder-fat.jar`
- Size: ~480 KB (application + dependencies)

### JRE Bundling

- Uses OpenJDK 11 (Temurin distribution)
- Portable, no system installation required
- Size: ~100-150 MB (depending on modules included)
- Users can use system Java if JRE detection fails

### Installer Size

Typical distribution package sizes:
- JAR only: ~500 KB
- JAR + JRE: ~130-200 MB
- ZIP archive: ~60-100 MB (compressed)

## Security Considerations

1. **Code Signing**
   - Consider signing the batch/PowerShell scripts
   - Windows Defender may warn about unsigned executables

2. **Antivirus Compatibility**
   - Some antivirus software may flag batch/PowerShell scripts
   - Signing can reduce false positives

3. **Registry Permissions**
   - Installer creates HKCU (user) registry entries, not HKLM (system)
   - Safer and doesn't require system-wide permissions

## Future Enhancements

1. **NSIS/Inno Setup Integration** - Create single .exe installer
2. **Code Signing** - Sign executables and scripts
3. **Update Mechanism** - Implement automatic updates
4. **Windows Store Integration** - Distribute via Microsoft Store
5. **Portable Version** - Create ZIP distribution without installer

## Related Files

- `pom.xml` - Maven build configuration
- `src/main/java/com/routefinder/MainApplication.java` - Application entry point
- `build.bat` - Build automation script
- `installer/` - Installation files

## References

- [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)
- [OpenJDK Temurin](https://adoptium.net/)
- [NSIS Installer](https://nsis.sourceforge.io/)
- [Windows Shortcuts](https://learn.microsoft.com/en-us/windows/win32/shell/shell-link-objects)
