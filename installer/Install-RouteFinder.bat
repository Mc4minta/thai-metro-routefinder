@echo off
REM RouteFinder Installer Wrapper
REM This script runs the PowerShell-based installer with proper privileges

setlocal enabledelayedexpansion
cd /d "%~dp0"

echo Installing RouteFinder...
powershell -ExecutionPolicy Bypass -File "RouteFinder-Installer.ps1"

if errorlevel 1 (
    echo.
    echo Installation failed. Please run this as Administrator.
    pause
    exit /b 1
)
