import json
import csv
import os

def update_fare_data():
    csv_path = r'src/main/resources/fare/price_matrices_formatted.csv'
    json_path = r'src/main/resources/data-fare-discount.json'
    
    if not os.path.exists(csv_path):
        print(f"CSV not found: {csv_path}")
        return
    if not os.path.exists(json_path):
        print(f"JSON not found: {json_path}")
        return

    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # 1. Add same-station edges (diagonal entries)
    with open(csv_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    current_section = None
    headers = []
    
    line_map = {
        'DARKRED': 'RN',
        'ARL': 'ARL',
        'PURPLE': 'PP',
        'BLUE LINE': 'BL',
        'BTS LIGHTGREEN': 'LG',
        'BTS DARKGREEN': 'DG',
        'YELLOW': 'YL',
        'PINK': 'PK'
    }

    new_edges = []
    
    # Track existing edges to avoid duplicates
    existing_edges = set()
    for edge in data.get('edges', []):
        if edge.get('type') == 'fare':
            existing_edges.add((edge['from'], edge['to'], edge['line']))

    for line in lines:
        line = line.strip()
        if not line: continue
        if line.startswith('**'):
            current_section = line.strip('*').upper()
            headers = []
            continue
        
        parts = line.split(',')
        if parts[0] == 'จาก \\ ไป':
            headers = parts[1:]
            continue
        
        if current_section and headers:
            from_id = parts[0]
            line_code = line_map.get(current_section)
            if not line_code: continue
            
            for i, to_id in enumerate(headers):
                if i + 1 >= len(parts): continue
                price_str = parts[i+1].strip()
                if not price_str: continue
                
                try:
                    price = float(price_str)
                except ValueError:
                    continue
                
                if from_id == to_id:
                    if (from_id, to_id, line_code) not in existing_edges:
                        new_edges.append({
                            "from": from_id,
                            "to": to_id,
                            "line": line_code,
                            "price": price,
                            "type": "fare"
                        })
                        existing_edges.add((from_id, to_id, line_code))

    data['edges'].extend(new_edges)
    print(f"Added {len(new_edges)} same-station fare edges.")

    # 2. Add Interchange Edges
    interchanges = [
        ("PP16", "PP", "BL10", "BL"), # Tao Poon
        ("CEN", "LG", "CEN", "DG"),   # Siam
        ("BL11", "BL", "RN01", "RN"), # Bang Sue / Krung Thep Aphiwat
        ("BL21", "BL", "A6", "ARL"),  # Phetchaburi / Makkasan
        ("N2", "LG", "A8", "ARL"),    # Phaya Thai
        ("BL22", "BL", "E4", "LG"),   # Sukhumvit / Asok
        ("BL26", "BL", "S2", "DG"),   # Silom / Sala Daeng
        ("BL13", "BL", "N8", "LG"),   # Chatuchak Park / Mo Chit
        ("N9", "LG", "BL14", "BL"),   # Ha Yaek Lat Phrao / Phahon Yothin
        ("BL15", "BL", "YL01", "YL"), # Lat Phrao
        ("PP11", "PP", "PK01", "PK"), # Nonthaburi Civic Center
        ("N17", "LG", "PK16", "PK"),  # Wat Phra Sri Mahathat
        ("RN06", "RN", "PK14", "PK"), # Laksi
        ("A4", "ARL", "YL11", "YL"),  # Hua Mak
        ("E15", "LG", "YL23", "YL"),  # Samrong
        ("BL34", "BL", "S12", "DG"),  # Bang Wa
    ]

    interchange_count = 0
    # Track existing interchanges
    existing_interchanges = set()
    for edge in data.get('edges', []):
        if edge.get('type') == 'interchange':
            existing_interchanges.add((edge['from'], edge['from_line'], edge['to'], edge['to_line']))

    for f_id, f_line, t_id, t_line in interchanges:
        if (f_id, f_line, t_id, t_line) not in existing_interchanges:
            data['edges'].append({
                "from": f_id,
                "from_line": f_line,
                "to": t_id,
                "to_line": t_line,
                "type": "interchange",
                "price": 0.0
            })
            existing_interchanges.add((f_id, f_line, t_id, t_line))
            interchange_count += 1
        
        if (t_id, t_line, f_id, f_line) not in existing_interchanges:
            data['edges'].append({
                "from": t_id,
                "from_line": t_line,
                "to": f_id,
                "to_line": f_line,
                "type": "interchange",
                "price": 0.0
            })
            existing_interchanges.add((t_id, t_line, f_id, f_line))
            interchange_count += 1

    print(f"Added {interchange_count} interchange edges.")

    # 3. Add Discount Table
    data['discounts'] = {
        "PP": {"BL": 14.0},
        "BL": {"PP": 14.0, "YL": 15.0, "PK": 15.0},
        "YL": {"BL": 15.0},
        "PK": {"BL": 15.0}
    }
    print("Added discounts table.")

    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=4, ensure_ascii=False)
    
    # Also update data-fare-new.json if it exists
    new_json_path = r'src/main/resources/data-fare-new.json'
    if os.path.exists(new_json_path):
        with open(new_json_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=4, ensure_ascii=False)
        print(f"Updated {new_json_path}")

if __name__ == "__main__":
    update_fare_data()
