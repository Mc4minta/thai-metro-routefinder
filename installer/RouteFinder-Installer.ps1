# RouteFinder Installer Script
# Run with: powershell -ExecutionPolicy Bypass -File RouteFinder-Installer.ps1

[CmdletBinding()]
param(
    [Parameter(Mandatory=$false)]
    [string]$InstallPath = "C:\Program Files\RouteFinder"
)

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Error-Custom {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
    exit 1
}

Write-Host "RouteFinder Installer" -ForegroundColor Cyan
Write-Host "=====================`n" -ForegroundColor Cyan

# Check if running as admin
if (-not ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "This installer requires administrator privileges." -ForegroundColor Yellow
    Write-Host "Attempting to elevate..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-ExecutionPolicy Bypass -File `"$PSCommandPath`"" -Verb RunAs
    exit
}

# Create install directory
if (Test-Path $InstallPath) {
    $response = Read-Host "$InstallPath already exists. Overwrite? (y/n)"
    if ($response -ne 'y') { exit }
    Remove-Item $InstallPath -Recurse -Force
}

New-Item -ItemType Directory -Path $InstallPath -Force | Out-Null
Write-Success "Created installation directory"

# Copy files
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Copy-Item "$scriptDir\routefinder-fat.jar" "$InstallPath\" -Force
Copy-Item "$scriptDir\RouteFinder.bat" "$InstallPath\" -Force
Copy-Item "$scriptDir\jre" "$InstallPath\" -Recurse -Force -ErrorAction SilentlyContinue
Write-Success "Copied application files"

# Create shortcuts
$startMenuPath = "$env:APPDATA\Microsoft\Windows\Start Menu\Programs\RouteFinder"
New-Item -ItemType Directory -Path $startMenuPath -Force | Out-Null

$WshShell = New-Object -ComObject WScript.Shell
$shortcut = $WshShell.CreateShortcut("$startMenuPath\RouteFinder.lnk")
$shortcut.TargetPath = "$InstallPath\RouteFinder.bat"
$shortcut.WorkingDirectory = $InstallPath
$shortcut.Save()
Write-Success "Created Start Menu shortcut"

# Create desktop shortcut
$desktopPath = "$env:USERPROFILE\Desktop"
$shortcut = $WshShell.CreateShortcut("$desktopPath\RouteFinder.lnk")
$shortcut.TargetPath = "$InstallPath\RouteFinder.bat"
$shortcut.WorkingDirectory = $InstallPath
$shortcut.Save()
Write-Success "Created Desktop shortcut"

# Create uninstaller script
$uninstallerContent = @"
`$InstallPath = "$InstallPath"
`$startMenuPath = "`$env:APPDATA\Microsoft\Windows\Start Menu\Programs\RouteFinder"

Remove-Item "`$startMenuPath\RouteFinder.lnk" -Force -ErrorAction SilentlyContinue
Remove-Item "`$startMenuPath" -Force -ErrorAction SilentlyContinue
Remove-Item "`$env:USERPROFILE\Desktop\RouteFinder.lnk" -Force -ErrorAction SilentlyContinue
Remove-Item `$InstallPath -Recurse -Force

Write-Host "RouteFinder uninstalled successfully" -ForegroundColor Green
"@

Set-Content -Path "$InstallPath\Uninstall.ps1" -Value $uninstallerContent
Write-Success "Created uninstaller"

Write-Host "`n" -ForegroundColor Green
Write-Host "Installation Complete!" -ForegroundColor Green
Write-Host "RouteFinder is ready to use. Look for it in your Start Menu or Desktop." -ForegroundColor Cyan
Read-Host "Press Enter to exit"
