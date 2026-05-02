# Real Fare Calculation Implementation Plan

This plan outlines the steps to replace the mock route calculation with a real fare calculation engine based on Dijkstra's algorithm and provided fare matrices.

## User Review Required

> [!IMPORTANT]
> The fare matrices in CSV format use Thai station names as headers. I will map these to the `name_th` field in `data.json`. Any mismatch in naming might lead to missing edges.

> [!NOTE]
> The discount logic (e.g., 14/15 THB off for specific transfers) will be hardcoded in a `FareService` as specified in `logic-new.md` unless a separate configuration file is provided.

## Proposed Changes

### [Component] Core Logic & Services

#### [NEW] [FareService.java](file:///c:/Users/admin/Documents/KMUTT/routefinder/src/main/java/com/routefinder/service/FareService.java)
- Load all CSV files from `src/main/resources/fare/`.
- Parse matrices and store them in a way that's easy to query (e.g., `Map<String, Map<String, Double>>` where keys are line codes and then station names).
- Implement the Dijkstra algorithm with the extended state `(station, line, afterInterchange)` as described in `logic-new.md`.
- Handle the transfer discounts (e.g., BL <-> PP, BL <-> YL/PK).

#### [MODIFY] [RouteController.java](file:///c:/Users/admin/Documents/KMUTT/routefinder/src/main/java/com/routefinder/controller/RouteController.java)
- Inject `FareService`.
- Replace `mockCalculate` with a call to `FareService.calculateRoute(start, end)`.
- Keep the existing `formatForUI` logic as it already follows the "UI grouping" principle.

#### [MODIFY] [StationDataService.java](file:///c:/Users/admin/Documents/KMUTT/routefinder/src/main/java/com/routefinder/service/StationDataService.java)
- Add a helper method to find a `Station` by its Thai name and line code (useful for mapping CSV headers).

### [Component] Main Application

#### [MODIFY] [MainApplication.java](file:///c:/Users/admin/Documents/KMUTT/routefinder/src/main/java/com/routefinder/MainApplication.java)
- Initialize `FareService` and pass it to `RouteController`.

## Verification Plan

### Automated Tests
- Since this is a GUI app, I will primarily verify by checking specific known routes:
    - **Single line**: Tao Poon to Bang Sue (Blue Line) -> Should be 17 THB.
    - **Interchange**: Tao Poon (Purple) to Bang Sue (Blue) -> Should apply the interchange discount if applicable.
    - **Long distance**: Lak Song to Tao Poon -> Should hit the cap (45 THB).

### Manual Verification
- Use the UI to select stations and verify that the "Total Fare" matches expected values from the CSVs.
- Verify that the step-by-step path correctly groups stations by line and shows transfers.
