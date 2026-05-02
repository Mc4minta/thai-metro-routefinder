# Bangkok Transit Route Finder

A Java Swing application that calculates the optimal route and precise fare across the Bangkok mass transit network (MRT, BTS, SRT, ARL).

## Overview

The Route Finder uses an **edge-weighted graph with Dijkstra’s Algorithm** to compute the minimum fare route between any two stations. It features a robust implementation of the official transit fare logic, correctly handling complex rules like cross-line transfer discounts.

## Features

- **Comprehensive Network Support**: Supports routing across 8 major transit lines:
  - MRT Blue Line (BL)
  - MRT Purple Line (PP)
  - MRT Pink Line (PK)
  - MRT Yellow Line (YL)
  - SRT Dark Red Line (RN)
  - Airport Rail Link (ARL)
  - BTS Sukhumvit Line (LG)
  - BTS Silom Line (DG)
- **Accurate Fare Engine**: Calculates exact travel costs utilizing explicitly defined fare matrices for each line.
- **Interchange & Discount Logic**: Automatically applies conditional discounts when transferring between compatible lines at official interchange stations (e.g., Tao Poon, Lat Phrao, Nonthaburi Civic Center).
- **Same-Station Routing**: Computes accurate minimum entry/exit fares if the start and end destinations are the same station.
- **Interactive UI**: A Model-View-Controller (MVC) compliant Java Swing interface that provides step-by-step route breakdowns, grouping consecutive stations by line, and clearly visualizing transfer points.

## Architecture & Algorithm

### Extended Dijkstra Algorithm

Standard Dijkstra's algorithm is extended to support conditional discounts. The algorithm tracks a composite state during pathfinding: `(Node, Cost, AfterInterchange)`.

- If a user traverses an `interchange` edge (Cost: 0 THB), the `AfterInterchange` flag is set to true.
- On the subsequent travel edge, the algorithm consults the discount matrix to reduce the incoming edge's fare if applicable.

### Strict Separation of Concerns

All routing and fare logic is strictly confined to the Graph engine (`FareService`). The UI serves purely as a presentation layer—it formats and groups the resulting edge sequences without recalculating or modifying costs.

### JSON Data Structure

The application's transit network is fully data-driven and dynamically loaded from `src/main/resources/data-fare-discount.json`, which contains:

- **Stations**: Metadata and UI display names for all nodes.
- **Edges**: Directional connections representing normal travel (with fare) and Interchange walks (price 0).
- **Discounts**: A mapping table of transfer discounts (e.g., `BL <-> PP = 14 THB`).

## Installation & Running

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven

### Running the Application

**Important**: Ensure you run the Maven commands from the root directory of the project (where the `pom.xml` file is located), *not* inside the `script/` folder.

1. Clone or download the repository.
2. Open a terminal/command prompt in the project's root directory.
3. Build the project:

   ```powershell
   mvn clean install
   ```

4. Run the application:

   ```powershell
   mvn compile exec:java "-Dexec.mainClass=com.routefinder.MainApplication"
   ```

## Development & Data Maintenance

If you need to update the fare prices, station definitions, or discount rules:

1. Update the relevant CSV matrices in `src/main/resources/fare/`.
2. Run the provided Python synchronization script to rebuild the JSON dataset:

   ```powershell
   # Run from the project root
   python script/update_fare_data.py
   ```

3. The application will automatically ingest the updated `data-fare-discount.json` on the next launch.

## License

This project is licensed under the MIT License.
