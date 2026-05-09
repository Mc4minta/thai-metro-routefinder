@echo off
REM RouteFinder Launcher
setlocal enabledelayedexpansion

REM Get the directory where this batch file is located
set SCRIPT_DIR=%~dp0
set JRE_PATH=%SCRIPT_DIR%jre\bin\java.exe
set JAR_PATH=%SCRIPT_DIR%routefinder-fat.jar

REM Check if JRE exists
if exist "!JRE_PATH!" (
    "!JRE_PATH!" -jar "!JAR_PATH!"
) else (
    echo Java Runtime Environment not found in: !JRE_PATH!
    echo Please ensure JRE is installed in the jre directory
    pause
    exit /b 1
)
