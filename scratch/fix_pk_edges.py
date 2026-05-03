import json
import csv
import os

data_path = 'src/main/resources/data.json'
pink_csv_path = 'src/main/resources/fare/price_pink.csv'

# Read existing data
with open(data_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

# Read pink line prices
prices = {}
with open(pink_csv_path, 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    headers = next(reader)
    
    for row in reader:
        from_id_or_name = row[0]
        # Map Thai names back to IDs
        if from_id_or_name == 'รามอินทรา กม.4':
            from_id = 'PK19'
        elif from_id_or_name == 'รามอินทรา กม.6':
            from_id = 'PK22'
        elif from_id_or_name == 'รามอินทรา กม.9':
            from_id = 'PK24'
        else:
            from_id = from_id_or_name
            
        prices[from_id] = {}
        for i, price_str in enumerate(row[1:]):
            to_id_or_name = headers[i+1]
            if to_id_or_name == 'รามอินทรา กม.4':
                to_id = 'PK19'
            elif to_id_or_name == 'รามอินทรา กม.6':
                to_id = 'PK22'
            elif to_id_or_name == 'รามอินทรา กม.9':
                to_id = 'PK24'
            else:
                to_id = to_id_or_name
            
            prices[from_id][to_id] = float(price_str)

# Stations to fix
stations_to_fix = ['PK19', 'PK22', 'PK24']

new_edges = []
for from_id in stations_to_fix:
    if from_id in prices:
        for to_id, price in prices[from_id].items():
            new_edges.append({
                "from": from_id,
                "to": to_id,
                "line": "PK",
                "price": price,
                "type": "fare"
            })
            
# Don't forget edges TO the isolated stations FROM other normal stations
for normal_id in prices:
    if normal_id not in stations_to_fix:
        for to_id in stations_to_fix:
            if to_id in prices[normal_id]:
                new_edges.append({
                    "from": normal_id,
                    "to": to_id,
                    "line": "PK",
                    "price": prices[normal_id][to_id],
                    "type": "fare"
                })

print(f"Adding {len(new_edges)} edges...")

# Insert new edges just before interchange edges (or append)
data['edges'].extend(new_edges)

# Write back
with open(data_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)
    
print("Done!")
