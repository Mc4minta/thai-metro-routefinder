@echo off
REM RouteFinder Build Script - Windows Batch Version
REM This script automates the creation of Windows installer

setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ==========================================
echo RouteFinder Build Automation Script
echo ==========================================
echo.

REM Step 1: Build fat JAR
echo Step 1: Building fat JAR with Maven...
call mvn clean package -DskipTests

if not exist "target\routefinder-fat.jar" (
    echo ERROR: Fat JAR not created
    exit /b 1
)
echo ^✓ Fat JAR created successfully
echo.

REM Step 2: Copy JAR to installer directory
echo Step 2: Preparing installer directory...
if not exist "installer" mkdir installer
copy /Y "target\routefinder-fat.jar" "installer\"
echo ^✓ JAR copied to installer directory
echo.

REM Step 3: Summary
echo ==========================================
echo Build Complete!
echo ==========================================
echo.
echo Installer files location:
echo   %CD%\installer
echo.
echo Installation methods:
echo   1. Windows Batch Installer (recommended):
echo      - Run: Install-RouteFinder.bat
echo.
echo   2. PowerShell Installer:
echo      - Run: powershell -ExecutionPolicy Bypass -File RouteFinder-Installer.ps1
echo.
echo   3. Manual Installation:
echo      - Copy the routefinder-fat.jar and jre folder to desired location
echo      - Run: RouteFinder.bat
echo.
echo Note: The installer automatically bundles the JRE (Java Runtime Environment)
echo       Users do not need to install Java separately.
echo.
pause
