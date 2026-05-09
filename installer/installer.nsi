; RouteFinder Windows Installer Script (NSIS)

!include "MUI2.nsh"

; Basic Settings
Name "RouteFinder"
OutFile "RouteFinder-1.0.0-installer.exe"
InstallDir "$PROGRAMFILES\RouteFinder"
InstallDirRegKey HKCU "Software\RouteFinder" "InstallDir"

; Request admin privileges
RequestExecutionLevel admin

; MUI Settings
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

; Installer section
Section "Install"
    SetOutPath "$INSTDIR"
    
    ; Copy launcher batch script
    File "RouteFinder.bat"
    
    ; Copy JAR file
    File "routefinder-fat.jar"
    
    ; Copy bundled JRE
    SetOutPath "$INSTDIR\jre"
    File /r "jre\*.*"
    
    ; Go back to install dir
    SetOutPath "$INSTDIR"
    
    ; Create shortcuts
    CreateDirectory "$SMPROGRAMS\RouteFinder"
    CreateShortCut "$SMPROGRAMS\RouteFinder\RouteFinder.lnk" "$INSTDIR\RouteFinder.bat"
    CreateShortCut "$SMPROGRAMS\RouteFinder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    
    ; Optional: Desktop shortcut
    CreateShortCut "$DESKTOP\RouteFinder.lnk" "$INSTDIR\RouteFinder.bat"
    
    ; Create uninstaller
    WriteUninstaller "$INSTDIR\Uninstall.exe"
    
    ; Registry entry for uninstall
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\RouteFinder" \
                     "DisplayName" "RouteFinder"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\RouteFinder" \
                     "UninstallString" "$INSTDIR\Uninstall.exe"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\RouteFinder" \
                     "DisplayVersion" "1.0.0"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\RouteFinder" \
                     "InstallLocation" "$INSTDIR"
SectionEnd

; Uninstaller section
Section "Uninstall"
    ; Remove shortcuts
    Delete "$SMPROGRAMS\RouteFinder\RouteFinder.lnk"
    Delete "$SMPROGRAMS\RouteFinder\Uninstall.lnk"
    RMDir "$SMPROGRAMS\RouteFinder"
    Delete "$DESKTOP\RouteFinder.lnk"
    
    ; Remove files
    RMDir /r "$INSTDIR\jre"
    Delete "$INSTDIR\RouteFinder.bat"
    Delete "$INSTDIR\routefinder-fat.jar"
    Delete "$INSTDIR\Uninstall.exe"
    RMDir "$INSTDIR"
    
    ; Remove registry entries
    DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\RouteFinder"
    DeleteRegKey HKCU "Software\RouteFinder"
SectionEnd
