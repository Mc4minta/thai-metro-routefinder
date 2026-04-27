# MRT Route Finder

MRT Route Finder is a Java Swing application that helps users find the shortest route between stations on the MRT (Mass Rapid Transit) system.

## Features

- **Route Planning**: Find the shortest route between two stations
- **Transfer Detection**: Automatically finds optimal transfer stations
- **User Interface**: Clean GUI with station search functionality

## Installation

1. Clone or download the repository
2. Open the project in IntelliJ IDEA
3. Run the `MainApplication` class

## Usage

1. Launch the application
2. Enter the starting station name
3. Enter the destination station name
4. Click "Find Route" to see the shortest path

## License

This project is licensed under the MIT License.

## To run

```powershell
mvn install
```

```powershell
mvn compile exec:java "-Dexec.mainClass=com.routefinder.MainApplication"
```
