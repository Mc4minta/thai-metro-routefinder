# Codebase Reference

This document describes the main source files in the project and explains the role of each class in English.

## `src/main/java/com/routefinder/MainApplication.java`

### `MainApplication`

Application entry point. It creates the data service, fare engine, controller, and main window, then shows the Swing UI on the Event Dispatch Thread.

Responsibilities:

- Bootstrap the MVC object graph
- Create shared services
- Open the main frame

## `src/main/java/com/routefinder/controller/RouteController.java`

### `RouteController`

Coordinates the user interface and backend services. It receives station selections from the UI, asks `FareService` to calculate the route, and formats the raw path into rows that the result panel can display.

Responsibilities:

- Expose line and station lists to the UI
- Trigger route calculation
- Group path edges into display-friendly steps
- Provide line colors to the UI
- Switch the view between input and result screens

## `src/main/java/com/routefinder/model/Line.java`

### `Line`

Represents a transit line such as Blue Line, Purple Line, or BTS Silom Line.

Fields:

- `code` - short line code such as `BL` or `DG`
- `name_th` - Thai display name
- `name_en` - English display name
- `color` - UI color used in the Swing components

## `src/main/java/com/routefinder/model/Station.java`

### `Station`

Represents a station on a specific line.

Fields:

- `id` - station identifier used in the dataset
- `name_th` - Thai display name
- `name_en` - English display name
- `line` - line code that owns the station record

## `src/main/java/com/routefinder/model/PathEdge.java`

### `PathEdge`

Represents one step in the calculated route. A path edge can be either a normal travel segment or an interchange edge.

Fields:

- `from` - starting station
- `to` - ending station
- `line` - line code used for this step
- `cost` - fare cost for the edge
- `isInterchange` - whether the edge is a zero-cost transfer

## `src/main/java/com/routefinder/service/StationDataService.java`

### `StationDataService`

Loads line and station metadata from `data.json`, then loads the line color palette from `color-code.json`.

Responsibilities:

- Parse transit lines from JSON
- Parse stations from JSON
- Map each line code to a UI color
- Provide stations filtered by line

## `src/main/java/com/routefinder/service/FareService.java`

### `FareService`

Core route engine. It loads the graph from `data.json`, adds extra direct BTS fare edges from CSV data, and calculates the cheapest route with Dijkstra's algorithm.

Responsibilities:

- Build the station graph from JSON
- Load direct fare data from the BTS matrix CSV
- Store interchange edges and transfer discounts
- Handle same-station fare cases
- Reconstruct the final path as a list of `PathEdge` objects

Important behavior:

- Each graph node is a pair of station ID and line code
- Interchange edges have zero cost
- Transfer discounts are applied only to the first travel edge after an interchange
- Self-edge fare cases are used only for same-station routing

## `src/main/java/com/routefinder/view/MainFrame.java`

### `MainFrame`

Top-level Swing window. It hosts the input panel and result panel using a `CardLayout`.

Responsibilities:

- Create the main application window
- Switch between input and result screens
- Pass calculated results to the result panel

## `src/main/java/com/routefinder/view/InputPanel.java`

### `InputPanel`

The route selection form. It lets the user choose start and end lines, then choose stations on those lines.

Responsibilities:

- Render the input form
- Populate station dropdowns based on the selected line
- Enable the calculate button only when both stations are selected
- Forward the route request to `RouteController`

Notes:

- The panel uses dynamic line colors for better visual feedback
- The current implementation is centered around Thai labels for the live UI

## `src/main/java/com/routefinder/view/ResultPanel.java`

### `ResultPanel`

Displays the calculated route as grouped line segments and transfer information.

Responsibilities:

- Render the result header and total fare
- Show grouped route steps
- Show transfer cards between lines
- Display a no-route state when no path is found

## `src/main/java/com/routefinder/view/Test.java`

### `Test`

Temporary or experimental Swing panel used for mock result rendering. It is not part of the main production flow.

Responsibilities:

- Demonstrate result panel styling
- Render mock route rows
- Provide an alternate visual test harness

## `src/test/java/com/routefinder/service/FareServiceTest.java`

### `FareServiceTest`

JUnit test suite for validating route calculation behavior.

Coverage includes:

- Direct same-line routes
- Interchange routes
- Same-station behavior
- Discount application after interchanges
- Blue Line direct fare correctness
- BTS direct fare behavior

## Resource Files

### `src/main/resources/data.json`

Primary data source for stations, lines, edges, and transfer discounts.

### `src/main/resources/color-code.json`

Maps line codes to RGB values for the UI.

### `src/main/resources/fare/*.csv`

Fare matrices and line-specific fare tables used by the fare engine and maintenance scripts.

### `src/main/resources/logic-new.md`

Design note that explains the graph logic and route calculation approach.

## Maintenance Notes

- Keep the route graph data and the UI display data in sync
- Add tests when fare matrices or transfer rules change
- Treat `FareService` as the source of truth for pricing logic
