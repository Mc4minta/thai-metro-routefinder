# implementation plan for 3 May 2026

## 1. Data source

- change the data use from data-fare-discount.json to data.json
- and then edit the data.json
- the CEN from DG should become CEN_DG
- nd the CEN from LG should become CEN_LG
- to avoid same name collision

## 2. add the same station logic

- it will says the fare from the table
- please explore the /main/resources/fare/
- that it's include self edge too
- as the table in C:\Users\admin\Documents\KMUTT\routefinder\src\main\resources\fare stated

## 3. make an "No route found" UX

## 4. Edit README.md to match the lastest data

## More details

```markdown
 Key incompleteness / improvements

   1. No automated tests (src/test is empty), so route/fare regressions are easy to miss.
   2. Route engine ignores destination line in goal check (FareService.calculateRoute, line curr.node.stationId.equals(end.id)), so same
  station ID on different lines can return wrong end-state semantics (notably CEN).
   3. “Same station” logic is incomplete: README says minimum entry/exit fare, but current behavior returns 0 with empty path.
   4. No “no route found” UX: unreachable cases currently end up as empty result + 0 fare.
   5. Data maintenance pipeline is fragile: scripts use absolute paths; CSV name normalization is brittle.
   6. README/script mismatch: README points to script/update_fare_data.py, but file is under src/main/resources/script/....

  Graph + Dijkstra check

  Yes. FareService implements:

   - Graph: adjacency list Map<Node, List<Edge>>
   - Min-cost search: PriorityQueue<State> with cost ordering
   - Extended Dijkstra state: (node, cost, afterInterchange) to apply post-transfer discounts

  Data structures & algorithms used (and fit)

  ┌──────────────────────────────────────────┬─────────────────────────────────┬──────────────────────────────────────────────────────────┐
  │ Item                                     │ Where                           │ Fit for use                                              │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ Map<Node, List<Edge>> adjacency list     │ FareService                     │ Good for routing graph                                   │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ PriorityQueue<State>                     │ FareService                     │ Good for Dijkstra                                        │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ Map<Node, Map<Boolean, Double>> dist     │ FareService                     │ Good, tracks two-state shortest distances                │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ Map<String, Map<String, Double>>         │ FareService                     │ Good for transfer discount lookup                        │
  │ discounts                                │                                 │                                                          │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ List<PathEdge> path reconstruction       │ FareService/RouteController     │ Good                                                     │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ List<Object[]> UI rows                   │ RouteController/ResultPanel     │ Works but weak typing; should be replaced by a typed DTO │
  ├──────────────────────────────────────────┼─────────────────────────────────┼──────────────────────────────────────────────────────────┤
  │ Single-pass grouping algorithm           │ RouteController.formatForUI     │ Good for presentation                                    │
  └──────────────────────────────────────────┴─────────────────────────────────┴──────────────────────────────────────────────────────────┘

  Bugs / potential bugs found

   1. Destination line ambiguity bug for multi-line same ID (CEN in DG/LG).
   2. Same-station fare bug (returns 0, contradicts stated feature).
   3. Unreachable-route silent failure (no explicit error).
   4. Data-source split risk: StationDataService reads data.json, while routing reads data-fare-discount.json; mismatch can break routing
  silently.

  Data errors found

  Your fare graph has 3 isolated stations (no incoming/outgoing edges):

   - PK19 (Ram Inthra Kor Mor 4)
   - PK22 (Ram Inthra Kor Mor 6)
   - PK24 (Ram Inthra Kor Mor 9)

  This is a real data integrity issue in data-fare-discount.json (stations exist but fare edges are missing).
  Likely source: unresolved Pink-line CSV identifiers (header cells still Thai names in fare/price_pink.csv).

  How to test route calculator (recommended test cases)

  Use these as baseline regression cases:

   1. Single-line fare
   - PP01 -> PP16
   - Expected: route found, fare 42.0
   2. Discount after interchange
   - PP16(PP) -> BL11(BL)
   - Expected: includes interchange at Tao Poon, fare 3.0 on first BL leg after discount
   3. Long single-line Blue
   - BL01 -> BL38
   - Expected: route found, fare 30.0
   4. Interchange-only move
   - BL15 -> YL01
   - Expected: 1 interchange step, fare 0.0
   5. Same station same line
   - PP16 -> PP16
   - Current behavior: 0.0 (flag as bug vs stated requirement)
   6. Same station different line (CEN)
   - CEN(DG) -> CEN(LG)
   - Current behavior: empty 0-cost result (flag ambiguity bug)
   7. Known broken data
   - PK19 -> PK20
   - Expected: currently no route (should fail until data fixed)

  Logic documentation (how it works)

   1. MainApplication wires MVC (StationDataService, FareService, RouteController, MainFrame).
   2. InputPanel collects start/end line+station selections.
   3. RouteController.onCalculateRoute() calls fareService.calculateRoute(start,end).
   4. FareService loads station/edge/discount JSON, runs Dijkstra with interchange-state discount logic, returns List<PathEdge>.
   5. Controller groups consecutive same-line edges for display and sums edge costs.
   6. ResultPanel renders grouped segments, transfer hints, and total fare.

  Highest-priority fixes:

   1. fix destination match to include line when needed, 2) implement same-station fare rule explicitly, 3) add no-route error handling, 4)
   repair PK19/PK22/PK24 fare edges in data pipeline, 5) add automated route regression tests.
```