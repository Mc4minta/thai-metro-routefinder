# Bangkok Rail Price Matrices

Format: `Price` then section blocks such as `**DARKRED**`, `**ARL**`, etc. Each block is a fare matrix with `จาก \ ไป` as the first header cell.

Notes:
- All fares are adult regular fares in Thai baht.
- DARKRED uses the official SRTET adult fare matrix.
- ARL uses the common 15-45 baht Airport Rail Link station-interval fare table.
- MRT Purple, Yellow, and Pink matrices follow the MRTA official fare matrices. Pink file here covers the main PK01-PK30 line; MT01/MT02 branch stations are not included in the matrix block because exact branch OD cells need the operator calculator / current board confirmation.
- Blue Line matrix is generated from the current MRTA station-count fare rule (17-45 baht) using the shortest path around the BL loop.
- BTS Green matrix is a rule-derived matrix using the 17-47 baht core concession, 17-45 baht extensions, and 65 baht current cap. Because BTS publishes exact OD via its interactive calculator, verify critical BTS OD cells with the BTS calculator before production use.
