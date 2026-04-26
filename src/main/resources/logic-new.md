# MRT / BTS / SRT Calculation Approach

## Overview

This system uses an **edge-weighted graph with Dijkstra’s Algorithm** to compute the minimum fare route.

> ✅ **Key Principle:**
>
> - All fare calculations are handled **inside the graph (edges)**
> - The UI must **NOT recalculate fares**
> - The UI is only responsible for **grouping and displaying results**

---

## Graph Representation

### Node Structure

**Node:** `(station, line)`

- Example:
  - `(PP16, PP)`
  - `(BL30, BL)`

> **IMPORTANT:**
> Same station but different lines are treated as **different nodes**
>
> Example:
>
> - `(CEN, DG)` ≠ `(CEN, LG)`

---

## Edge Representation

Stored as an **Adjacency List**:

```

(node) → [(toNode, weight)]

```

There are two types of edges:

---

### 1. Same Line Travel

```

(station A, LINE) → (station B, LINE)

```

- **Weight:** Fare from fare table
- **Scope:** Can connect **any station within the same line** (not just adjacent)

> ✅ This simplifies pathfinding — Dijkstra handles all routing via cost comparison

---

### 2. Interchange (Walk)

```

(station, LINE1) → (station, LINE2)

```

- **Weight:** `0`

**Purpose:**

- Allows switching between lines
- Does **NOT** directly apply a discount
- Only sets state for the next movement

---

## Discount Handling

Discounts are stored as:

```

Map<fromLine, Map<toLine, discount>>

```

### Example

```

discount["PP"]["BL"] = 14
discount["BL"]["PP"] = 14
discount["BL"]["YL"] = 15

```

---

### Rules

- Discount is applied **ONLY AFTER an interchange**
- Discount is applied to the **FIRST travel edge after switching lines**
- If no mapping exists → discount = `0`

---

## Dijkstra State

To support conditional discounts, the algorithm tracks extended state:

```

(node, cost, afterInterchange)

````

### afterInterchange (Boolean)

| Value | Meaning |
|------|--------|
| `true` | Next travel edge can receive discount |
| `false` | Normal travel |

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
````

---

## Path Output (IMPORTANT)

The result of Dijkstra is a sequence of **edges**, not grouped segments.

### Example Output Path

```
(PP16, PP) → (PP15, PP) → (PP14, PP) → (PP14, BL) → (BL10, BL)
```

Each step already includes:

- Correct fare
- Discount (if applicable)

---

## UI Responsibility (CRITICAL DESIGN RULE)

### ❌ What UI MUST NOT do

- Recalculate fare
- Call fare table again
- Apply discount again

---

### ✅ What UI SHOULD do

- Group consecutive edges with same line
- Sum **existing edge costs only**
- Display transfers

---

### Example Transformation

#### Raw Path (from Dijkstra)

```
A → B (PP, 10)
B → C (PP, 8)
C → C (BL, 0)   // interchange
C → D (BL, 5)   // discounted
```

#### UI Output

```
PP: A → C (18)
🔁 เปลี่ยนสายจาก PP ไป BL ที่สถานี C
BL: C → D (5)
```

---

## Key Architecture Insight

```
Graph (Dijkstra) = Source of Truth for Cost
UI = Presentation Layer only
```

> If UI starts recalculating fares → system becomes inconsistent

---

## Summary

| Component    | Responsibility            |
| ------------ | ------------------------- |
| Graph        | Define all fare logic     |
| Dijkstra     | Compute cheapest path     |
| Edge weights | Store actual cost         |
| UI           | Format and display result |

---

## Future Improvements (Optional)

- Replace `Object[]` with strong typed model (e.g., `PathSegment`)
- Add metadata for interchange stations
- Support time-based routing (not just fare)

---

If you want next step, I’d strongly suggest:

👉 refactor from `Object[]` → proper class like:

```java
class PathEdge {
    Station from;
    Station to;
    String line;
    double cost;
}
```
