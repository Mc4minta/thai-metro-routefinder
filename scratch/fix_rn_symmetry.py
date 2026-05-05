import json

def fix_rn_fares(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Extract RN fare edges
    rn_fares = {}
    other_edges = []
    for edge in data['edges']:
        if edge.get('line') == 'RN' and edge.get('type') == 'fare':
            rn_fares[(edge['from'], edge['to'])] = edge
        else:
            other_edges.append(edge)
            
    # Make symmetric
    station_ids = [f'RN{i:02d}' for i in range(1, 11)]
    fixed_edges = []
    
    for i, s1 in enumerate(station_ids):
        for j, s2 in enumerate(station_ids):
            e12 = rn_fares.get((s1, s2))
            e21 = rn_fares.get((s2, s1))
            
            if s1 == s2:
                # Self-edge, keep as is or set to min base?
                price = min(e12['price'] if e12 else 12.0, e21['price'] if e21 else 12.0)
            else:
                p12 = e12['price'] if e12 else 999.0
                p21 = e21['price'] if e21 else 999.0
                price = min(p12, p21)
                
            fixed_edges.append({
                "from": s1,
                "to": s2,
                "line": "RN",
                "price": price,
                "type": "fare"
            })
            
    data['edges'] = other_edges + fixed_edges
    
    with open(file_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    print("Fixed RN fares symmetry in data.json")

if __name__ == '__main__':
    fix_rn_fares('src/main/resources/data.json')
