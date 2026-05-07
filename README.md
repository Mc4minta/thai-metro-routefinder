# RouteFinder

RouteFinder is a Java Swing desktop application for finding transit routes and fares across Bangkok's rail network, including MRT, BTS, SRT, and ARL lines.

## What It Does

- Finds the cheapest route between two selected stations
- Computes fares using a data-driven graph loaded from `src/main/resources/data.json`
- Applies interchange rules and transfer discounts when needed
- Displays grouped route steps and transfer points in a Swing UI

## Tech Stack

- Java 11+
- Maven
- Swing for the desktop UI
- Gson for JSON loading
- JUnit 5 for tests

## Project Structure

- `src/main/java/com/routefinder/MainApplication.java` - application entry point
- `src/main/java/com/routefinder/controller/RouteController.java` - UI controller and orchestration layer
- `src/main/java/com/routefinder/model/` - domain models for lines, stations, and path edges
- `src/main/java/com/routefinder/service/FareService.java` - route search and fare calculation engine
- `src/main/java/com/routefinder/service/StationDataService.java` - station and line data loader
- `src/main/java/com/routefinder/view/` - Swing UI panels and main window
- `src/test/java/com/routefinder/service/FareServiceTest.java` - route calculation regression tests
- `docs/codebase-reference.md` - file-by-file and class-by-class documentation

## Architecture

The app follows a simple MVC-style structure:

- `view` renders the UI and forwards user actions
- `controller` coordinates the request and prepares display data
- `service` contains the routing and fare logic
- `model` stores the data objects used across the app

The route engine uses an edge-weighted graph with Dijkstra's algorithm. Each node represents a specific station on a specific line, which allows the app to handle same-station interchanges and transfer discounts correctly.

## Running the App

### Prerequisites

- Java Development Kit (JDK) 11 or newer
- Maven

### Build And Test

Run these commands from the project root, where `pom.xml` is located:

```powershell
mvn clean test
```

### Launch

If you want to run the desktop app from Maven, use:

```powershell
mvn -Dexec.mainClass=com.routefinder.MainApplication exec:java
```

If your Maven setup does not already have the Exec plugin available, open the project in an IDE and run `com.routefinder.MainApplication` directly.

## Data Files

The application is driven by the resources under `src/main/resources/`:

- `data.json` - station, line, edge, and discount data
- `color-code.json` - line color mapping for the UI
- `fare/` - fare matrix CSV files used to maintain route pricing data
- `logic-new.md` - routing logic notes for the graph engine

## Updating Fare Or Station Data

1. Update the source CSV or JSON data under `src/main/resources/`
2. Rebuild or refresh any generated data files if needed
3. Run the tests again with `mvn test`

## Documentation

For a detailed English reference of each source file and class, see:

- `docs/codebase-reference.md`

## License

This project is licensed under the MIT License.
