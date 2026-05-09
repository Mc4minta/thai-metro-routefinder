# 🚀 RouteFinder Build Quick Reference

## For End Users: Installing RouteFinder

### Method 1: Easy (Recommended)
```cmd
Install-RouteFinder.bat
```
✓ Double-click the file
✓ Follow prompts
✓ Done!

### Method 2: PowerShell
```powershell
powershell -ExecutionPolicy Bypass -File RouteFinder-Installer.ps1
```

### Method 3: Manual
1. Copy folder to desired location
2. Double-click `RouteFinder.bat`

---

## For Developers: Building the Installer

### Quick Build
```powershell
cd C:\Users\admin\Documents\KMUTT\routefinder
.\build.bat
```

### Manual Build
```powershell
git checkout build
mvn clean package -DskipTests
```

### Output
```
installer/
├── RouteFinder.bat              ← Launcher
├── RouteFinder-Installer.ps1    ← Installer
├── routefinder-fat.jar          ← Application
└── jre/                         ← Java Runtime
```

---

## Installation Details

| Aspect | Details |
|--------|---------|
| **Install Location** | `C:\Program Files\RouteFinder` |
| **Start Menu** | RouteFinder shortcut |
| **Desktop** | Optional shortcut |
| **Uninstall** | Start Menu > Programs > Uninstall |
| **Size** | ~130-170 MB |
| **Requirements** | Windows 7+ |
| **Java Needed** | No (bundled) |

---

## File Descriptions

| File | Size | Purpose |
|------|------|---------|
| `RouteFinder.bat` | 470 B | Launches application |
| `RouteFinder-Installer.ps1` | 3.3 KB | PowerShell installer |
| `Install-RouteFinder.bat` | 400 B | Batch installer wrapper |
| `routefinder-fat.jar` | 471 KB | Application package |
| `jre/` | 130 MB | Java runtime |

---

## Git Branch Info

```bash
# View branch
git branch

# Switch to build branch
git checkout build

# See recent commits
git log --oneline -5
```

**Current Branch:** `build`
**Latest Commit:** Automates Windows installer creation

---

## Troubleshooting

### App won't start
1. Check `jre\bin\java.exe` exists
2. Run as Administrator
3. Check Windows Defender logs

### Installer fails
1. Run as Administrator
2. Need 200+ MB disk space
3. Check Windows Defender quarantine

### Need help?
→ See `INSTALLER_GUIDE.md` for full documentation

---

## One-Command Instructions

**For Developers (Build Everything):**
```powershell
.\build.bat
```

**For Users (Install Application):**
```powershell
.\Install-RouteFinder.bat
```

**For Users (Uninstall Application):**
```powershell
# Control Panel → Programs → Uninstall RouteFinder
# OR run: C:\Program Files\RouteFinder\Uninstall.ps1
```

---

## Key Technologies

- **Java** - 11+ (bundled)
- **Maven** - Build system
- **PowerShell** - Installer automation
- **OpenJDK Temurin** - Java runtime

---

## Project Structure

```
routefinder/
├── pom.xml                 ← Build configuration
├── build.bat              ← Build script
├── INSTALLER_GUIDE.md     ← Full documentation
└── installer/
    ├── RouteFinder.bat
    ├── RouteFinder-Installer.ps1
    ├── routefinder-fat.jar
    └── jre/
```

---

## Common Commands

| Task | Command |
|------|---------|
| Build | `.\build.bat` |
| Install | `.\Install-RouteFinder.bat` |
| Run App | `.\installer\RouteFinder.bat` |
| View Logs | Check Event Viewer |
| Report Bug | See documentation |

---

**Status:** ✅ Complete and Ready  
**Version:** 1.0.0  
**Date Created:** 2026-05-09  
**Last Updated:** 2026-05-09
