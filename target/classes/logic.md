# MRT / BTS / SRT Calculation Approach

## Graph Representation

Edge-heavy approach with single-layer logic using **Dijkstra's Algorithm**.

### Node Structure

**Node:** `(station, line)`

* Example: `(PP16, PP)`, `(BL30, BL)`

> **IMPORTANT:** Same station but different line = different node (e.g., `(CEN, DG)` and `(CEN, LG)`).

---

## Edge Representation

Stored as an **Adjacency List**: `(to, weight)`. There are two types of edges:

### 1. Same Line Travel

`(station A, LINE) → (station B, LINE)`

* **Weight:** Fare from table.
* **Logic:** Includes every station in the same line (not just adjacent stations). This allows Dijkstra to handle all pathfinding through simple table lookups.

### 2. Interchange (Walk)

`(station, LINE1) → (station, LINE2)`

* **Weight:** $0$
* **IMPORTANT:** * Interchange has **NO** fare.
  * It only allows switching lines.
  * It does **NOT** directly apply a discount; it sets a state for the next travel.

---

## Discount Handling

Used for interchanging between lines via a mapping: `Map<startline, <endline, discount>>`.

* **Example:** * `discount["PP"]["BL"] = 14`
  * `discount["BL"]["PP"] = 14`
  * `discount["BL"]["YL"] = 15`
* **IMPORTANT:**
  * Discount is **NOT** applied on the interchange edge.
  * Discount is applied on the **FIRST** travel **AFTER** the interchange.
  * If no mapping exists, `discount = 0`.

---

## Dijkstra State

The state must track more than just the current node to handle conditional discounts.

**State:** `(node, cost, afterInterchange)`

* **afterInterchange (Boolean):**
  * `true`: Next travel can receive a discount.
  * `false`: Normal travel.

---

## Dijkstra Logic

```text
FOR each edge e from current.node DO

    nextNode ← e.to
    nextLine ← nextNode.line
    newCost ← current.cost

    // CASE 1: Interchange (same station, different line)
    IF current.node.station = nextNode.station 
    AND current.node.line ≠ nextLine THEN
        
        newCost ← newCost + 0
        nextAfterInterchange ← true

    ELSE
        
        // CASE 2: Normal travel
        travelCost ← e.weight

        // Apply discount ONLY if coming from interchange
        IF current.afterInterchange = true THEN
            
            discountValue ← discount[current.node.line][nextLine]
            
            IF discountValue does not exist THEN
                discountValue ← 0
            ENDIF

            travelCost ← travelCost - discountValue

            // Prevent negative cost
            IF travelCost < 0 THEN
                travelCost ← 0
            ENDIF
            
        ENDIF

        newCost ← newCost + travelCost
        nextAfterInterchange ← false

    ENDIF

    // Relaxation with extended state
    IF newCost < dist[nextNode][nextAfterInterchange] THEN
        dist[nextNode][nextAfterInterchange] ← newCost
        PUSH (nextNode, newCost, nextAfterInterchange) INTO priority queue
    ENDIF

END FOR
