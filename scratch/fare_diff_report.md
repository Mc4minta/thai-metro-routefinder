# Fare Matrix Diff Report

Source comparison: `src/main/resources/data.json` vs `src/main/resources/fare/price_matrices_formatted.csv`

## Summary
- DARKRED / RN: 44 mismatched cells
- ARL / ARL: 0 mismatched cells
- PURPLE / PP: 0 mismatched cells
- BLUE LINE / BL: 0 mismatched cells
- BTS lightgreen / LG: 0 mismatched cells (matches after CEN normalization)
- BTS darkgreen / DG: 0 mismatched cells (matches after CEN normalization)
- YELLOW / YL: 0 mismatched cells
- PINK / PK: 90 mismatched cells

## DARKRED / RN
Mismatched cells: 44

| From | To | CSV | JSON | Delta |
| --- | --- | ---: | ---: | ---: |
| RN02 | RN01 | 22 | 12 | -10 |
| RN03 | RN01 | 25 | 16 | -9 |
| RN03 | RN02 | 19 | 12 | -7 |
| RN04 | RN01 | 26 | 19 | -7 |
| RN04 | RN02 | 20 | 15 | -5 |
| RN04 | RN03 | 17 | 12 | -5 |
| RN05 | RN01 | 29 | 20 | -9 |
| RN05 | RN02 | 23 | 17 | -6 |
| RN05 | RN03 | 19 | 14 | -5 |
| RN05 | RN04 | 16 | 12 | -4 |
| RN06 | RN01 | 33 | 23 | -10 |
| RN06 | RN02 | 27 | 19 | -8 |
| RN06 | RN03 | 23 | 16 | -7 |
| RN06 | RN04 | 20 | 14 | -6 |
| RN06 | RN05 | 19 | 12 | -7 |
| RN07 | RN01 | 36 | 27 | -9 |
| RN07 | RN02 | 30 | 23 | -7 |
| RN07 | RN03 | 26 | 20 | -6 |
| RN07 | RN04 | 23 | 19 | -4 |
| RN07 | RN05 | 22 | 17 | -5 |
| RN07 | RN06 | 20 | 12 | -8 |
| RN08 | RN01 | 39 | 33 | -6 |
| RN08 | RN02 | 33 | 26 | -7 |
| RN08 | RN03 | 29 | 23 | -6 |
| RN08 | RN04 | 26 | 22 | -4 |
| RN08 | RN05 | 25 | 20 | -5 |
| RN08 | RN06 | 23 | 15 | -8 |
| RN08 | RN07 | 18 | 12 | -6 |
| RN09 | RN01 | 42 | 33 | -9 |
| RN09 | RN02 | 42 | 29 | -13 |
| RN09 | RN03 | 38 | 26 | -12 |
| RN09 | RN04 | 35 | 25 | -10 |
| RN09 | RN05 | 34 | 23 | -11 |
| RN09 | RN06 | 32 | 18 | -14 |
| RN09 | RN07 | 27 | 15 | -12 |
| RN09 | RN08 | 24 | 12 | -12 |
| RN10 | RN02 | 42 | 38 | -4 |
| RN10 | RN03 | 42 | 35 | -7 |
| RN10 | RN04 | 40 | 34 | -6 |
| RN10 | RN05 | 38 | 32 | -6 |
| RN10 | RN06 | 36 | 27 | -9 |
| RN10 | RN07 | 32 | 24 | -8 |
| RN10 | RN08 | 29 | 21 | -8 |
| RN10 | RN09 | 26 | 12 | -14 |

## PINK / PK
Mismatched cells: 90

| From | To | CSV | JSON | Delta |
| --- | --- | ---: | ---: | ---: |
| PK09 | PK19 | 45 | 42 | -3 |
| PK10 | PK19 | 42 | 40 | -2 |
| PK11 | PK19 | 39 | 37 | -2 |
| PK11 | PK22 | 45 | 44 | -1 |
| PK12 | PK19 | 37 | 34 | -3 |
| PK12 | PK22 | 44 | 42 | -2 |
| PK13 | PK19 | 34 | 31 | -3 |
| PK13 | PK22 | 42 | 40 | -2 |
| PK14 | PK22 | 40 | 37 | -3 |
| PK14 | PK24 | 45 | 43 | -2 |
| PK15 | PK22 | 37 | 35 | -2 |
| PK15 | PK24 | 43 | 41 | -2 |
| PK16 | PK22 | 35 | 32 | -3 |
| PK16 | PK24 | 41 | 39 | -2 |
| PK17 | PK22 | 32 | 27 | -5 |
| PK17 | PK24 | 39 | 34 | -5 |
| PK18 | PK22 | 27 | 24 | -3 |
| PK18 | PK24 | 34 | 30 | -4 |
| PK19 | PK09 | 45 | 42 | -3 |
| PK19 | PK10 | 42 | 40 | -2 |
| PK19 | PK11 | 39 | 37 | -2 |
| PK19 | PK12 | 37 | 34 | -3 |
| PK19 | PK13 | 34 | 31 | -3 |
| PK19 | PK22 | 24 | 22 | -2 |
| PK19 | PK23 | 27 | 25 | -2 |
| PK19 | PK24 | 30 | 28 | -2 |
| PK19 | PK25 | 35 | 32 | -3 |
| PK19 | PK26 | 39 | 37 | -2 |
| PK19 | PK27 | 44 | 38 | -6 |
| PK19 | PK28 | 45 | 41 | -4 |
| PK19 | PK29 | 45 | 42 | -3 |
| PK20 | PK22 | 22 | 18 | -4 |
| PK20 | PK24 | 28 | 25 | -3 |
| PK21 | PK22 | 18 | 15 | -3 |
| PK21 | PK24 | 25 | 22 | -3 |
| PK22 | PK11 | 45 | 44 | -1 |
| PK22 | PK12 | 44 | 42 | -2 |
| PK22 | PK13 | 42 | 40 | -2 |
| PK22 | PK14 | 40 | 37 | -3 |
| PK22 | PK15 | 37 | 35 | -2 |
| PK22 | PK16 | 35 | 32 | -3 |
| PK22 | PK17 | 32 | 27 | -5 |
| PK22 | PK18 | 27 | 24 | -3 |
| PK22 | PK19 | 24 | 22 | -2 |
| PK22 | PK20 | 22 | 18 | -4 |
| PK22 | PK21 | 18 | 15 | -3 |
| PK22 | PK23 | 18 | 15 | -3 |
| PK22 | PK24 | 22 | 19 | -3 |
| PK22 | PK25 | 26 | 23 | -3 |
| PK22 | PK26 | 30 | 28 | -2 |
| PK22 | PK27 | 36 | 29 | -7 |
| PK22 | PK28 | 38 | 32 | -6 |
| PK22 | PK29 | 42 | 35 | -7 |
| PK22 | PK30 | 45 | 35 | -10 |
| PK23 | PK19 | 27 | 25 | -2 |
| PK23 | PK22 | 18 | 15 | -3 |
| PK23 | PK24 | 19 | 15 | -4 |
| PK24 | PK14 | 45 | 43 | -2 |
| PK24 | PK15 | 43 | 41 | -2 |
| PK24 | PK16 | 41 | 39 | -2 |
| PK24 | PK17 | 39 | 34 | -5 |
| PK24 | PK18 | 34 | 30 | -4 |
| PK24 | PK19 | 30 | 28 | -2 |
| PK24 | PK20 | 28 | 25 | -3 |
| PK24 | PK21 | 25 | 22 | -3 |
| PK24 | PK22 | 22 | 19 | -3 |
| PK24 | PK23 | 19 | 15 | -4 |
| PK24 | PK25 | 19 | 15 | -4 |
| PK24 | PK26 | 24 | 21 | -3 |
| PK24 | PK27 | 29 | 21 | -8 |
| PK24 | PK28 | 32 | 24 | -8 |
| PK24 | PK29 | 36 | 27 | -9 |
| PK24 | PK30 | 39 | 25 | -14 |
| PK25 | PK19 | 35 | 32 | -3 |
| PK25 | PK22 | 26 | 23 | -3 |
| PK25 | PK24 | 19 | 15 | -4 |
| PK26 | PK19 | 39 | 37 | -2 |
| PK26 | PK22 | 30 | 28 | -2 |
| PK26 | PK24 | 24 | 21 | -3 |
| PK27 | PK19 | 44 | 38 | -6 |
| PK27 | PK22 | 36 | 29 | -7 |
| PK27 | PK24 | 29 | 21 | -8 |
| PK28 | PK19 | 45 | 41 | -4 |
| PK28 | PK22 | 38 | 32 | -6 |
| PK28 | PK24 | 32 | 24 | -8 |
| PK29 | PK19 | 45 | 42 | -3 |
| PK29 | PK22 | 42 | 35 | -7 |
| PK29 | PK24 | 36 | 27 | -9 |
| PK30 | PK22 | 45 | 35 | -10 |
| PK30 | PK24 | 39 | 25 | -14 |

## Naming-only differences
- `LG` and `DG` match the CSV values after normalizing `CEN_LG` / `CEN_DG` to `CEN`.

## Notes
- `RN` and `PK` contain genuine fare mismatches across many cells.
- Station order and matrix dimensions match for all sections.