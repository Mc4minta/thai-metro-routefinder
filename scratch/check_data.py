import json
from collections import defaultdict

def check_data_consistency(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    stations = {s['id']: s for s in data['stations']}
    lines = {l['code']: l for l in data['lines']}
    
    errors = []
    warnings = []
    
    adj = defaultdict(list)
    
    # 1. Check Stations
    for sid, s in stations.items():
        if s['line'] not in lines:
            errors.append(f"Station {sid} has invalid line {s['line']}")
        if sid == 'CEN':
            errors.append(f"Station ID 'CEN' found! Should be CEN_DG or CEN_LG")
            
    # 2. Check Edges
    for i, edge in enumerate(data['edges']):
        f = edge.get('from')
        t = edge.get('to')
        etype = edge.get('type')
        price = edge.get('price', 0)
        
        if f not in stations:
            errors.append(f"Edge {i} ({etype}): 'from' station {f} not found")
        if t not in stations:
            errors.append(f"Edge {i} ({etype}): 'to' station {t} not found")
            
        adj[f].append(edge)

        if etype == 'fare':
            eline = edge.get('line')
            if not eline:
                errors.append(f"Edge {i} (fare): missing 'line'")
            elif eline not in lines:
                errors.append(f"Edge {i} (fare): invalid line {eline}")
            
            if f == t:
                # warnings.append(f"Self-edge found for station {f} with price {price}")
                pass
            
            if price == 0:
                warnings.append(f"Zero price fare edge from {f} to {t}")

        elif etype == 'interchange':
            if not edge.get('from_line') or not edge.get('to_line'):
                errors.append(f"Edge {i} (interchange): missing from_line/to_line")
            if f == t and edge.get('from_line') == edge.get('to_line'):
                warnings.append(f"Edge {i} (interchange): identical from/to station and line ({f} {edge.get('from_line')})")

    # 3. Check for disconnected stations
    for sid in stations:
        if sid not in adj:
            is_target = False
            for edge in data['edges']:
                if edge['to'] == sid:
                    is_target = True
                    break
            if not is_target:
                warnings.append(f"Isolated station found: {sid} ({stations[sid]['name_en']})")

    # 4. Check for one-way edges (for fare edges)
    fare_edges = {}
    for edge in data['edges']:
        if edge.get('type') == 'fare':
            fare_edges[(edge['from'], edge['to'], edge.get('line'))] = edge.get('price')
            
    for (f, t, l), p in fare_edges.items():
        if f == t: continue
        if (t, f, l) not in fare_edges:
            warnings.append(f"One-way fare edge: {f} -> {t} (Line {l})")
        elif fare_edges[(t, f, l)] != p:
            warnings.append(f"Asymmetric fare: {f}->{t} ({p}) vs {t}->{f} ({fare_edges[(t, f, l)]})")

    return errors, warnings

if __name__ == '__main__':
    errors, warnings = check_data_consistency('src/main/resources/data.json')
    
    print(f"--- Data Consistency Report ---")
    if errors:
        print(f"Found {len(errors)} Errors:")
        for e in errors: print(f"  [ERROR] {e}")
    else:
        print("No Errors found.")
        
    if warnings:
        print(f"Found {len(warnings)} Warnings:")
        for w in warnings: print(f"  [WARNING] {w}")
    else:
        print("No Warnings found (excluding self-edges).")
